package br.com.rchlo.cards.services;

import br.com.rchlo.cards.domain.Card;
import br.com.rchlo.cards.domain.Transaction;
import br.com.rchlo.cards.dto.TransactionRequestDto;
import br.com.rchlo.cards.repositories.TransactionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.NoResultException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class FraudTooFast implements TransactionValidation{

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
