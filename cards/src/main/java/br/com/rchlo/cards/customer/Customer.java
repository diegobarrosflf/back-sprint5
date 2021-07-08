package br.com.rchlo.cards.customer;

import javax.persistence.Embeddable;

@Embeddable
public class Customer {

    private String fullName;
    private String address;
    private String email;

    /** @deprecated  */
    protected Customer() { }

    public Customer(String fullName, String address, String email) {
        this.fullName = fullName;
        this.address = address;
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public String getAddress() {
        return address;
    }

    public String getEmail() {
        return email;
    }

}
