package br.com.rchlo.cards.card.service;

import br.com.rchlo.cards.card.Card;
import br.com.rchlo.cards.fraudVerifier.FraudVerifier;
import br.com.rchlo.cards.transaction.Transaction;
import br.com.rchlo.cards.transaction.dto.TransactionRequestDto;
import br.com.rchlo.cards.card.repository.CardRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CardService {

    private CardRepository cardRepository;
    private List<FraudVerifier> enabledFraudVerifiers;

    public CardService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    public Card findByNumberNameExpirationAndCode(TransactionRequestDto transactionRequest) {
        return cardRepository.findByNumberNameExpirationAndCode(
                transactionRequest.getCardNumber(),
                transactionRequest.getCardHolderName(),
                transactionRequest.getCardExpiration().toString(),
                transactionRequest.getCardSecurityCode()
        ).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid card"));
    }

    public void hasLimit(TransactionRequestDto transactionRequest, Card card) {
        if(transactionRequest.getAmount().compareTo(card.getAvailableLimit()) > 0 ) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Limit not available");
        }
    }

    public void updateLimit(Transaction transaction) {
        Card card = transaction.getCard();
        var amount = transaction.getAmount();
        card.updateLimit(amount);
    }
}
