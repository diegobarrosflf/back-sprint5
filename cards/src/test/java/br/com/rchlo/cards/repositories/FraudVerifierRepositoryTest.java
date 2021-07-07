package br.com.rchlo.cards.repositories;

import br.com.rchlo.cards.builders.FraudVerifierBuilder;
import br.com.rchlo.cards.domain.FraudVerifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql("/schema.sql")
@ActiveProfiles(value = "test")
class FraudVerifierRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private FraudVerifierRepository fraudVerifierRepository;

    @BeforeEach
    void loadData() {
        aFraudVerifier();
        anotherFraudVerifier();
    }

    @Test
    void shouldFindAllEnabledTrue() {
        List<FraudVerifier> listFrauds = fraudVerifierRepository.findAllEnabledTrue();

        assertEquals(2, listFrauds.size());

        var expectedType = FraudVerifier.Type.EXPENDS_ALL_LIMIT;
        var retunedType = listFrauds.get(0).getType();
        assertEquals(expectedType, retunedType);

        expectedType = FraudVerifier.Type.TOO_FAST;
        retunedType = listFrauds.get(1).getType();
        assertEquals(expectedType, retunedType);
    }

    private FraudVerifier aFraudVerifier() {
       FraudVerifier fraudVerifier = new FraudVerifierBuilder()
                .withType(FraudVerifier.Type.EXPENDS_ALL_LIMIT)
                .withEnable(true)
                .build();
       testEntityManager.persist(fraudVerifier);
       return fraudVerifier;
    }

    private FraudVerifier anotherFraudVerifier() {
        FraudVerifier fraudVerifier = new FraudVerifierBuilder()
                .withType(FraudVerifier.Type.TOO_FAST)
                .withEnable(true)
                .build();
        testEntityManager.persist(fraudVerifier);
        return fraudVerifier;
    }

}