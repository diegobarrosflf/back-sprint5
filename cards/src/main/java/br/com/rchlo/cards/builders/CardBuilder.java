package br.com.rchlo.cards.builders;

import br.com.rchlo.cards.domain.Card;
import br.com.rchlo.cards.domain.Customer;

import java.math.BigDecimal;

public class CardBuilder {

    private String holderName;
    private String number;
    private String expiration;
    private String securityCode;
    private String issuingCompany;
    private BigDecimal monthlyFee;
    private BigDecimal totalLimit;
    private BigDecimal availableLimit;
    private Customer customer;

    public CardBuilder withHolderName(String holderName) {
        this.holderName = holderName;
        return this;
    }

    public CardBuilder withNumber(String number) {
        this.number = number;
        return this;
    }

    public CardBuilder withExpiration(String expiration) {
        this.expiration = expiration;
        return this;
    }

    public CardBuilder withSecurityCode(String securityCode) {
        this.securityCode = securityCode;
        return this;
    }

    public CardBuilder withIssuingCompany(String issuingCompany) {
        this.issuingCompany = issuingCompany;
        return this;
    }

    public CardBuilder withMonthlyFee(BigDecimal monthlyFee) {
        this.monthlyFee = monthlyFee;
        return this;
    }

    public CardBuilder withTotalLimit(BigDecimal totalLimit) {
        this.totalLimit = totalLimit;
        return this;
    }

    public CardBuilder withCustomer(Customer customer) {
        this.customer = customer;
        return this;
    }

    public CardBuilder withAvailableLimit(BigDecimal availableLimit) {
        this.availableLimit = availableLimit;
        return this;
    }

    public Card build() {
        return new Card(
                holderName,
                number,
                expiration,
                securityCode,
                issuingCompany,
                monthlyFee,
                totalLimit,
                availableLimit,
                customer);
    }


}

