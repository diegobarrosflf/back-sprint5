package br.com.rchlo.cards.domain;

import br.com.rchlo.cards.services.FraudExpendsAllLimit;
import br.com.rchlo.cards.services.FraudTooFast;
import br.com.rchlo.cards.services.TransactionValidation;

import javax.persistence.*;

@Entity
public class FraudVerifier {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Type type;

    private boolean enabled;

    public enum Type {
        EXPENDS_ALL_LIMIT{
            @Override
            public TransactionValidation getValidation() {
                return new FraudExpendsAllLimit();
            }
        },
        TOO_FAST{
            @Override
            public TransactionValidation getValidation() {
                return new FraudTooFast();
            }
        };

        public abstract TransactionValidation getValidation();
    }

    public FraudVerifier () {

    }

    public FraudVerifier(Type type, boolean enabled) {
        this.type = type;
        this.enabled = enabled;
    }

    public Type getType() {
        return type;
    }
}
