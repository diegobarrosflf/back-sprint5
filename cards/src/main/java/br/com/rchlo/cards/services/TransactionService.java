package br.com.rchlo.cards.services;

import br.com.rchlo.cards.domain.Transaction;
import br.com.rchlo.cards.repositories.TransactionRepository;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public void save(Transaction transaction) {
        transactionRepository.save(transaction);
    }

    public Transaction findById(String uuid) {
        long id = Long.parseLong(uuid);
        return transactionRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST));
    }

    public String creatNotification(Transaction transaction, Configuration freemarker) {
        String notificationText = "";
        try {
            Template template = freemarker.getTemplate("expense-notification.ftl");
            Map<String, Object> data = new HashMap<>();
            data.put("transaction", transaction);
            StringWriter out = new StringWriter();
            template.process(data, out);
            return notificationText = out.toString();
        } catch (IOException | TemplateException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public void sendNotification(MailSender mailSender,
                                 String notificationText,
                                 Transaction transaction) {

        var card = transaction.getCard();
        var message = new SimpleMailMessage();
        message.setFrom("noreply@rchlo.com.br");
        message.setTo(card.getCustomer().getEmail());
        message.setSubject("Nova despesa: " + transaction.getDescription());
        message.setText(notificationText);
        mailSender.send(message);   // para verificar o email enviado acesse: https://www.smtpbucket.com/emails.
        // Coloque noreply@rchlo.com.br em Sender e o email do cliente no Recipient.

    }
}
