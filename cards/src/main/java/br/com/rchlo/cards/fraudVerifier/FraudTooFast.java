package br.com.rchlo.cards.fraudVerifier;

import br.com.rchlo.cards.card.Card;
import br.com.rchlo.cards.transaction.Transaction;
import br.com.rchlo.cards.transaction.dto.TransactionRequestDto;
import br.com.rchlo.cards.transaction.repository.TransactionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.NoResultException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class FraudTooFast implements TransactionValidation {

    public void valid(Card card,
                      TransactionRepository transactionRepository,
                      TransactionRequestDto transactionRequest) {
        // fraude: duas transacoes com menos de 30 segundos
            LocalDateTime timeOfLastConfirmedTransactionForCard = null;
            try {
                timeOfLastConfirmedTransactionForCard =
                        transactionRepository
                                .findTimeOfLastConfirmedTransactionForCard(card, Transaction.Status.CONFIRMED);
            } catch (NoResultException ex) {
            }
            if (timeOfLastConfirmedTransactionForCard != null) {
                long secondsFromLastConfirmedTransactionForCard = ChronoUnit.SECONDS.between(timeOfLastConfirmedTransactionForCard, LocalDateTime.now());
                if (secondsFromLastConfirmedTransactionForCard < 30) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fraud detected");
                }
            }

    }
}
