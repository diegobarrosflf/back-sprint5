package br.com.rchlo.cards.customer.builder;

import br.com.rchlo.cards.customer.Customer;

public class CustomerBuilder {

    private String fullName;
    private String address;
    private String email;

    public CustomerBuilder withFullName(String fullName) {
        this.fullName = fullName;
        return this;
    }

    public CustomerBuilder withAddress(String address) {
        this.address = address;
        return this;
    }

    public CustomerBuilder withEmail(String email) {
        this.email = email;
        return this;
    }

    public Customer build() {
        return new Customer(fullName, address, email);
    }

}
