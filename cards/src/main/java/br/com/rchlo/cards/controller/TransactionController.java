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

        //recuperar a transação pelo id
        var transaction = transactionService.findById(uuid);

        transaction.confirm();

        // atualiza limite do cartao
        cardService.updateLimit(transaction);

        // inicio criacao texto de notificacao
        String notificationText = transactionService.creatNotification(transaction, freemarker);


        // inicio envio notificacao por email
        transactionService.sendNotification(mailSender, notificationText, transaction);

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
