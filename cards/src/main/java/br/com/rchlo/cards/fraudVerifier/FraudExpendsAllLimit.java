package br.com.rchlo.cards.fraudVerifier;

import br.com.rchlo.cards.card.Card;
import br.com.rchlo.cards.transaction.dto.TransactionRequestDto;
import br.com.rchlo.cards.transaction.repository.TransactionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class FraudExpendsAllLimit implements TransactionValidation {

    public void valid(Card card,
                      TransactionRepository transactionRepository,
                      TransactionRequestDto transactionRequest) {
            // fraude: gastar o limite de uma vez
            if (transactionRequest.getAmount().compareTo(card.getAvailableLimit()) == 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fraud detected");
            }
    }


}
