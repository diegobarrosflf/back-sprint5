package br.com.rchlo.cards.card;

import br.com.rchlo.cards.customer.Customer;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
public class Card {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String holderName;
    private String number;
    private String expiration;
    private String securityCode;
    private String issuingCompany;

    private BigDecimal monthlyFee;

    private BigDecimal totalLimit;
    private BigDecimal availableLimit;

    @Embedded
    private Customer customer;

    /** @deprecated */
    protected Card() {
    }

    public Card(String holderName, String number, String expiration, String securityCode, String issuingCompany, BigDecimal monthlyFee, BigDecimal totalLimit, BigDecimal availableLimit, Customer customer) {
        this.holderName = holderName;
        this.number = number;
        this.expiration = expiration;
        this.securityCode = securityCode;
        this.issuingCompany = issuingCompany;
        this.monthlyFee = monthlyFee;
        this.totalLimit = totalLimit;
        this.availableLimit = availableLimit;
        this.customer = customer;
    }

    public BigDecimal getAvailableLimit() {
        return availableLimit;
    }

    public void setAvailableLimit(BigDecimal availableLimit) {
        this.availableLimit = availableLimit;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void updateLimit(BigDecimal amount) {
        this.availableLimit = this.availableLimit.subtract(amount);
    }

    public String getNumber() {
        return this.number;
    }

    public String getHolderName() {
        return holderName;
    }

    public String getExpiration() {
        return expiration;
    }

    public String getSecurityCode() {
        return securityCode;
    }
}
