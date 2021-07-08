package br.com.rchlo.cards.fraudVerifier;

import br.com.rchlo.cards.card.Card;
import br.com.rchlo.cards.transaction.dto.TransactionRequestDto;
import br.com.rchlo.cards.transaction.repository.TransactionRepository;

public interface TransactionValidation {
    void valid(Card card,
               TransactionRepository transactionRepository,
               TransactionRequestDto transactionRequest);
}
