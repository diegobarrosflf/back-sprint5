package br.com.rchlo.cards.services;

import br.com.rchlo.cards.domain.Card;
import br.com.rchlo.cards.dto.TransactionRequestDto;
import br.com.rchlo.cards.repositories.TransactionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class FraudExpendsAllLimit implements TransactionValidation{

    public void valid(Card card,
                      TransactionRepository transactionRepository,
                      TransactionRequestDto transactionRequest) {
            // fraude: gastar o limite de uma vez
            if (transactionRequest.getAmount().compareTo(card.getAvailableLimit()) == 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fraud detected");
            }
    }


}
