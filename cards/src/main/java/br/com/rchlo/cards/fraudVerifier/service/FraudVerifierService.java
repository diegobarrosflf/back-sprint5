package br.com.rchlo.cards.fraudVerifier.service;

import br.com.rchlo.cards.card.Card;
import br.com.rchlo.cards.fraudVerifier.FraudVerifier;
import br.com.rchlo.cards.transaction.dto.TransactionRequestDto;
import br.com.rchlo.cards.fraudVerifier.repository.FraudVerifierRepository;
import br.com.rchlo.cards.transaction.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FraudVerifierService {

    private final FraudVerifierRepository fraudVerifierRepository;
    private final List<FraudVerifier> enabledFraudVerifiers;
    private final TransactionRepository transactionRepository;


    public FraudVerifierService(FraudVerifierRepository fraudVerifierRepository,
                                TransactionRepository transactioRepository) {
        this.fraudVerifierRepository = fraudVerifierRepository;
        this.enabledFraudVerifiers = fraudVerifierRepository.findAllEnabledTrue();
        this.transactionRepository = transactioRepository;
    }

    public void hasVerifiedFraud(@Valid TransactionRequestDto transactionRequest, Card card) {

        var validations = enabledFraudVerifiers
                .stream()
                .map(f -> f.getType().getValidation())
                .collect(Collectors.toList());

        validations.forEach(
                v -> v.valid(card, transactionRepository, transactionRequest)
        );

    }
}

