package br.com.rchlo.cards.services;

import br.com.rchlo.cards.domain.Card;
import br.com.rchlo.cards.domain.FraudVerifier;
import br.com.rchlo.cards.domain.Transaction;
import br.com.rchlo.cards.dto.TransactionRequestDto;
import br.com.rchlo.cards.repositories.FraudVerifierRepository;
import br.com.rchlo.cards.repositories.TransactionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.NoResultException;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class FraudVerifierService {

    private final FraudVerifierRepository fraudVerifierRepository;
    private final List<FraudVerifier> enabledFraudVerifiers;
    private TransactionRepository transactioRepository;


    public FraudVerifierService(FraudVerifierRepository fraudVerifierRepository, TransactionRepository transactioRepository) {
        this.fraudVerifierRepository = fraudVerifierRepository;
        this.enabledFraudVerifiers = fraudVerifierRepository.findAllEnabledTrue();
        this.transactioRepository = transactioRepository;
    }

    public void hasVerifiedFraud(@Valid TransactionRequestDto transactionRequest, Card card) {

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
                timeOfLastConfirmedTransactionForCard = transactioRepository.findTimeOfLastConfirmedTransactionForCard(card, Transaction.Status.CONFIRMED);
            } catch (NoResultException ex) { }
            if (timeOfLastConfirmedTransactionForCard != null) {
                long secondsFromLastConfirmedTransactionForCard = ChronoUnit.SECONDS.between(timeOfLastConfirmedTransactionForCard, LocalDateTime.now());
                if (secondsFromLastConfirmedTransactionForCard < 30) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fraud detected");
                }
            }
        }

    }
}

