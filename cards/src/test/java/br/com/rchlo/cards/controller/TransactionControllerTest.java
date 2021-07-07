package br.com.rchlo.cards.controller;

import br.com.rchlo.cards.builders.CardBuilder;
import br.com.rchlo.cards.builders.CustomerBuilder;
import br.com.rchlo.cards.builders.TransactionBuilder;
import br.com.rchlo.cards.domain.Card;
import br.com.rchlo.cards.domain.Customer;
import br.com.rchlo.cards.domain.Transaction;
import br.com.rchlo.cards.repositories.CardRepository;
import br.com.rchlo.cards.repositories.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql("/schema.sql")
@ActiveProfiles("test")
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CardRepository cardRepository;

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
    void NotShouldDetailTransactionWithInvalidUuuid() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/transaction/{uuid}", "asdasd564654"))
                .andExpect(MockMvcResultMatchers.status().is(404));

    }


    private Transaction aTransaction(Card card) {
        Transaction transaction = new TransactionBuilder()
                .withAmount(new BigDecimal("149.0"))
                .withDescription("Jeans transado da moda")
                .withCard(card)
                .build();
        transactionRepository.save(transaction);
        return transaction;
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
        cardRepository.save(card);
        return card;
    }

}