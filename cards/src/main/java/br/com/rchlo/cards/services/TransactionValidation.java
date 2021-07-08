package br.com.rchlo.cards.services;

import br.com.rchlo.cards.domain.Card;
import br.com.rchlo.cards.dto.TransactionRequestDto;
import br.com.rchlo.cards.repositories.TransactionRepository;

public interface TransactionValidation {
    void valid(Card card,
               TransactionRepository transactionRepository,
               TransactionRequestDto transactionRequest);
}
