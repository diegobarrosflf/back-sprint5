package br.com.rchlo.cards.fraudVerifier.builder;

import br.com.rchlo.cards.fraudVerifier.FraudVerifier;

public class FraudVerifierBuilder extends FraudVerifier {

    private Type type;
    private boolean enable;

    public FraudVerifierBuilder withType(Type type) {
        this.type = type;
        return this;
    }

    public FraudVerifierBuilder withEnable(boolean enable) {
        this.enable = enable;
        return this;
    }

    public FraudVerifier build() {
        return new FraudVerifier(type, enable);
    }
}
