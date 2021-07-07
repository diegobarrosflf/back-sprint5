package br.com.rchlo.cards.domain;

import javax.persistence.*;

@Entity
public class FraudVerifier {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Type type;

    private boolean enabled;

    public enum Type {
        EXPENDS_ALL_LIMIT,
        TOO_FAST;
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
