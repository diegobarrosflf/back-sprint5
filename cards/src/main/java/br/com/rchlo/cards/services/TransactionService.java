package br.com.rchlo.cards.services;

import br.com.rchlo.cards.domain.Transaction;
import br.com.rchlo.cards.repositories.TransactionRepository;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public void save(Transaction transaction) {
        transactionRepository.save(transaction);
    }
}
