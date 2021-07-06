package br.com.rchlo.cards.controller;

import br.com.rchlo.cards.domain.Card;
import br.com.rchlo.cards.domain.Transaction;
import br.com.rchlo.cards.dto.TransactionRequestDto;
import br.com.rchlo.cards.dto.TransactionResponseDto;
import br.com.rchlo.cards.repositories.CardRepository;
import br.com.rchlo.cards.services.CardService;
import br.com.rchlo.cards.services.FraudVerifierService;
import br.com.rchlo.cards.services.TransactionService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
public class TransactionController {

    private final EntityManager entityManager;
    private final Configuration freemarker;
    private final MailSender mailSender;
    private final CardRepository cardRepository;
    private final CardService cardService;
    private final FraudVerifierService fraudVerifierService;
    private final TransactionService transactionService;

    public TransactionController(EntityManager entityManager,
                                 Configuration freemarker,
                                 MailSender mailSender,
                                 CardRepository cardRepository,
                                 CardService cardService,
                                 FraudVerifierService fraudVerifierService,
                                 TransactionService transactionService) {
        this.entityManager = entityManager;
        this.freemarker = freemarker;
        this.mailSender = mailSender;
        this.cardRepository = cardRepository;
        this.cardService = cardService;
        this.fraudVerifierService = fraudVerifierService;
        this.transactionService = transactionService;
    }

    @GetMapping("/transactions/{uuid}")
    public ResponseEntity<TransactionResponseDto> detail(@PathVariable("uuid") String uuid) {
        Optional<Transaction> possibleTransaction = entityManager.createQuery("select t from Transaction t where t.uuid = :uuid", Transaction.class)
                .setParameter("uuid", uuid)
                .getResultStream().findFirst();
        Transaction transaction = possibleTransaction.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return ResponseEntity.ok().body(new TransactionResponseDto(transaction));
    }

    @Transactional
    @PostMapping("/transactions")
    public ResponseEntity<TransactionResponseDto> create(@RequestBody @Valid TransactionRequestDto transactionRequest,
                                                         UriComponentsBuilder uriBuilder) {

        //recuperar o cartão do banco se não existir lança uma Exception
        Card card = cardService.findByNumberNameExpirationAndCode(transactionRequest);

        // verificacao limite disponivel
        cardService.hasLimit(transactionRequest, card);

        // verificacao de fraude
        fraudVerifierService.hasVerifiedFraud(transactionRequest, card);

        // salva transacao
        Transaction transaction = transactionRequest.asEntity(card);
        transactionService.save(transaction);

        URI uri = uriBuilder.path("/transactions/{uuid}").buildAndExpand(transaction.getUuid()).toUri();
        return ResponseEntity.created(uri).body(new TransactionResponseDto(transaction));
    }

    @Transactional
    @PutMapping("/transactions/{uuid}")
    public ResponseEntity<Void> confirm(@PathVariable("uuid") String uuid) {
        Optional<Transaction> possibleTransaction = entityManager.createQuery("select t from Transaction t where t.uuid = :uuid", Transaction.class)
                .setParameter("uuid", uuid)
                .getResultStream().findFirst();
        Transaction transaction = possibleTransaction.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        transaction.confirm();


        // atualiza limite do cartao
        Card card = transaction.getCard();
        var amount = transaction.getAmount();
        card.updateLimit(amount);


        // inicio criacao texto de notificacao
        String notificationText = "";
        try {
            Template template = freemarker.getTemplate("expense-notification.ftl");
            Map<String, Object> data = new HashMap<>();
            data.put("transaction", transaction);
            StringWriter out = new StringWriter();
            template.process(data, out);
            notificationText = out.toString();
        } catch (IOException | TemplateException ex) {
            throw new IllegalStateException(ex);
        }
        // fim criacao texto de notificacao

        // inicio envio notificacao por email
        var message = new SimpleMailMessage();
        message.setFrom("noreply@rchlo.com.br");
        message.setTo(card.getCustomer().getEmail());
        message.setSubject("Nova despesa: " + transaction.getDescription());
        message.setText(notificationText);
        mailSender.send(message);   // para verificar o email enviado acesse: https://www.smtpbucket.com/emails.
                                    // Coloque noreply@rchlo.com.br em Sender e o email do cliente no Recipient.
        // fim envio notificacao por email

        return ResponseEntity.ok().build();
    }

    @Transactional
    @DeleteMapping("/transactions/{uuid}")
    public ResponseEntity<Void> cancel(@PathVariable("uuid") String uuid) {
        Optional<Transaction> possibleTransaction = entityManager.createQuery("select t from Transaction t where t.uuid = :uuid", Transaction.class)
                .setParameter("uuid", uuid)
                .getResultStream().findFirst();
        Transaction transaction = possibleTransaction.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        transaction.cancel();
        return ResponseEntity.ok().build();
    }

}
