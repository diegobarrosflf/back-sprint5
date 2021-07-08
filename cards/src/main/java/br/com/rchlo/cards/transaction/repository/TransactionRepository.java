package br.com.rchlo.cards.transaction.repository;

import br.com.rchlo.cards.card.Card;
import br.com.rchlo.cards.transaction.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("select max(t.createdAt) from Transaction " +
            " t where t.card = :card " +
            " and t.status = :status")
    LocalDateTime findTimeOfLastConfirmedTransactionForCard(Card card, Transaction.Status status);

    @Query("select t from Transaction t where t.uuid = :uuid")
    Optional<Transaction> findByUuid(String uuid);
}
