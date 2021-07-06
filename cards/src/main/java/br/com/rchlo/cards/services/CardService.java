package br.com.rchlo.cards.services;

import br.com.rchlo.cards.domain.Card;
import br.com.rchlo.cards.domain.FraudVerifier;
import br.com.rchlo.cards.dto.TransactionRequestDto;
import br.com.rchlo.cards.repositories.CardRepository;
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
}
