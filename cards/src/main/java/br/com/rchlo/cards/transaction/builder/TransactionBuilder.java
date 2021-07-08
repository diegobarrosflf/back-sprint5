package br.com.rchlo.cards.transaction.builder;

import br.com.rchlo.cards.card.Card;
import br.com.rchlo.cards.transaction.Transaction;

import java.math.BigDecimal;

public class TransactionBuilder{


    private BigDecimal amount;
    private String description;
    private Card card;


    public TransactionBuilder withAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public TransactionBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public TransactionBuilder withCard(Card card) {
        this.card = card;
        return this;
    }

    public Transaction build() {
        return new Transaction(
                amount,
                description,
                card
        );
    }

}
