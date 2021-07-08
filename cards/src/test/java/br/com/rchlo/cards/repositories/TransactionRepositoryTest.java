package br.com.rchlo.cards.repositories;

import br.com.rchlo.cards.card.builder.CardBuilder;
import br.com.rchlo.cards.customer.builder.CustomerBuilder;
import br.com.rchlo.cards.transaction.builder.TransactionBuilder;
import br.com.rchlo.cards.card.Card;
import br.com.rchlo.cards.customer.Customer;
import br.com.rchlo.cards.transaction.Transaction;
import br.com.rchlo.cards.transaction.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql("/schema.sql")
@ActiveProfiles(value = "test")
class TransactionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TransactionRepository transactionRepository;

    private Customer customer;
    private Card card;
    private Transaction transaction;


    @BeforeEach
    public void loadData() throws Exception {
        this.customer = aCustomer();
        this.card = aCard(customer);
        this.transaction = aTransaction(card);
    }

    @Test
    void shouldFindTimeOfLastConfirmedTransactionForCard() {
        transaction.confirm();
        LocalDateTime timeOfLastConfirmedTransaction = transactionRepository.findTimeOfLastConfirmedTransactionForCard(card, Transaction.Status.CONFIRMED);
        assertNotNull(timeOfLastConfirmedTransaction);
        assertThat(timeOfLastConfirmedTransaction).isBefore(LocalDateTime.now());

    }

    private Card aCard(Customer customer){
        Card card = new CardBuilder()
                .withHolderName("Fulano")
                .withNumber("3214654898756542")
                .withExpiration("2022-06")
                .withSecurityCode("123")
                .withIssuingCompany("Visa")
                .withMonthlyFee(new BigDecimal("1000.0"))
                .withTotalLimit(new BigDecimal("1000.0"))
                .withAvailableLimit(new BigDecimal("1000.0"))
                .withCustomer(customer)
                .build();
        entityManager.persist(card);
        return card;
    }

    private Customer aCustomer(){
        Customer customer = new CustomerBuilder()
                .withFullName("Fulano Da Silva Sauro")
                .withEmail("fulano123@gmail.com")
                .withAddress("Rua ABC, 123, São Paulo-SP").build();
        return customer;
    }

    private Transaction aTransaction(Card card) {
        Transaction transaction = new TransactionBuilder()
                .withAmount(new BigDecimal("149.0"))
                .withDescription("Jeans transado da moda")
                .withCard(card)
                .build();
        entityManager.persist(transaction);
        return transaction;
    }
}