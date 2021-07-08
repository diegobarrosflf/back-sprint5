package br.com.rchlo.cards.fraudVerifier.repository;

import br.com.rchlo.cards.fraudVerifier.FraudVerifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FraudVerifierRepository extends JpaRepository<FraudVerifier, Long> {

    @Query("select fv from FraudVerifier fv where fv.enabled = true")
    List<FraudVerifier> findAllEnabledTrue();
}
