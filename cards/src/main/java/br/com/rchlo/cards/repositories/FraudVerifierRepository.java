package br.com.rchlo.cards.repositories;

import br.com.rchlo.cards.domain.Card;
import br.com.rchlo.cards.domain.FraudVerifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FraudVerifierRepository extends JpaRepository<FraudVerifier, Long> {

    @Query("select fv from FraudVerifier fv where fv.enabled = true")
    List<FraudVerifier> findAllEnabledTrue();
}
