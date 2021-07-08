package br.com.rchlo.cards.card.repository;

import br.com.rchlo.cards.card.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {
    @Query("select c from Card c where" +
            " c.number = :number " +
            " and c.holderName = :name" +
            " and c.expiration = :expiration" +
            " and c.securityCode = :code")
    Optional<Card> findByNumberNameExpirationAndCode(
            String number,
            String name,
            String expiration,
            String code);
}
