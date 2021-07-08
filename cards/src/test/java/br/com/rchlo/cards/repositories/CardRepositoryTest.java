package br.com.rchlo.cards.repositories;

import br.com.rchlo.cards.card.builder.CardBuilder;
import br.com.rchlo.cards.card.repository.CardRepository;
import br.com.rchlo.cards.customer.builder.CustomerBuilder;
import br.com.rchlo.cards.card.Card;
import br.com.rchlo.cards.customer.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql("/schema.sql")
@ActiveProfiles(value = "test")
public class CardRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CardRepository cardRepository;

    @BeforeEach
    public void loadData() throws Exception {
        Customer customer = aCustomer();
        aCard(customer);
    }

    @Test
    public void shouldFindByNumberNameExpirationAndCode() {

        Card expectedCard = cardRepository.findByNumberNameExpirationAndCode(
                "3214654898756542",
                "Fulano",
                "2022-06",
                "123"
        ).get();

        assertNotNull(expectedCard);
        assertEquals(expectedCard.getNumber(), "3214654898756542");
        assertEquals(expectedCard.getHolderName(), "Fulano");
        assertEquals(expectedCard.getExpiration(), "2022-06");
        assertEquals(expectedCard.getSecurityCode(), "123");

    }

    private Customer aCustomer(){
        Customer customer = new CustomerBuilder()
                .withFullName("Fulano Da Silva Sauro")
                .withEmail("fulano123@gmail.com")
                .withAddress("Rua ABC, 123, São Paulo-SP").build();
        return customer;
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

}