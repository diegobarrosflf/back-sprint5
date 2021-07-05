package br.com.rchlo.cards.controller;

import br.com.rchlo.cards.domain.Card;
import br.com.rchlo.cards.domain.FraudVerifier;
import br.com.rchlo.cards.domain.Transaction;
import br.com.rchlo.cards.dto.TransactionRequestDto;
import br.com.rchlo.cards.dto.TransactionResponseDto;
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
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class TransactionController {

    private final EntityManager entityManager;
    private final Configuration freemarker;
    private final MailSender mailSender;

    public TransactionController(EntityManager entityManager, Configuration freemarker, MailSender mailSender) {
        this.entityManager = entityManager;
        this.freemarker = freemarker;
        this.mailSender = mailSender;
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
    public ResponseEntity<TransactionResponseDto> create(@RequestBody @Valid TransactionRequestDto transactionRequest, UriComponentsBuilder uriBuilder) {

        // inicio validacao se card existe
        Optional<Card> possibleCard = entityManager.createQuery("select c from Card c where c.number = :number " +
                " and c.holderName = :name and c.expiration = :expiration and c.securityCode = :code", Card.class)
                .setParameter("number", transactionRequest.getCardNumber())
                .setParameter("name", transactionRequest.getCardHolderName())
                .setParameter("expiration", transactionRequest.getCardExpiration().toString())
                .setParameter("code", transactionRequest.getCardSecurityCode())
                .getResultStream().findFirst();
        Card card = possibleCard.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid card"));
        // fim validacao se card existe

        // inicio verificacao limite disponivel
        if(transactionRequest.getAmount().compareTo(card.getAvailableLimit()) > 0 ) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Limit not available");
        }
        // fim verificacao limite disponivel

        // inicio verificacao de fraude
        List<FraudVerifier> enabledFraudVerifiers = entityManager.createQuery("select fv from FraudVerifier fv where fv.enabled = true", FraudVerifier.class)
                .getResultList();

        // fraude: gastar o limite de uma vez
        if (enabledFraudVerifiers.stream().map(FraudVerifier::getType)
                .anyMatch(FraudVerifier.Type.EXPENDS_ALL_LIMIT::equals)) {
            if(transactionRequest.getAmount().compareTo(card.getAvailableLimit()) == 0 ) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fraud detected");
            }
        }

        // fraude: duas transacoes com menos de 30 segundos
        if (enabledFraudVerifiers.stream().map(FraudVerifier::getType)
                .anyMatch(FraudVerifier.Type.TOO_FAST::equals)) {
            LocalDateTime timeOfLastConfirmedTransactionForCard = null;
            try {
                timeOfLastConfirmedTransactionForCard = entityManager.createQuery("select max(t.createdAt) from Transaction  t where t.card = :card " +
                        " and t.status = :status", LocalDateTime.class)
                        .setParameter("card", card)
                        .setParameter("status", Transaction.Status.CONFIRMED).getSingleResult();
            } catch (NoResultException ex) { }
            if (timeOfLastConfirmedTransactionForCard != null) {
                long secondsFromLastConfirmedTransactionForCard = ChronoUnit.SECONDS.between(timeOfLastConfirmedTransactionForCard, LocalDateTime.now());
                if (secondsFromLastConfirmedTransactionForCard < 30) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fraud detected");
                }
            }
        }
        // fim verificacao de fraude

        // salva transacao
        Transaction transaction = transactionRequest.asEntity(card);
        entityManager.persist(transaction);

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

        if (!Transaction.Status.CREATED.equals(transaction.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid transaction status");
        }

        transaction.setStatus(Transaction.Status.CONFIRMED);

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

        if (!Transaction.Status.CREATED.equals(transaction.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid transaction status");
        }

        transaction.setStatus(Transaction.Status.CANCELED);
        return ResponseEntity.ok().build();
    }
}
