package br.com.rchlo.cards.transaction.dto;

import br.com.rchlo.cards.card.Card;
import br.com.rchlo.cards.transaction.Transaction;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.YearMonth;

public class TransactionRequestDto {

    @NotNull
    @Positive
    @JsonProperty
    private BigDecimal amount;

    @NotBlank
    @JsonProperty
    private String description;

    @NotBlank
    @Size(max = 100)
    @JsonProperty
    private String cardHolderName;

    @NotBlank
    @Pattern(regexp = "\\d{16}")
    @JsonProperty
    private String cardNumber;

    @NotNull
    @Future
    @JsonProperty
    private YearMonth cardExpiration;

    @NotBlank
    @Pattern(regexp = "\\d{3}")
    @JsonProperty
    private String cardSecurityCode;

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public YearMonth getCardExpiration() {
        return cardExpiration;
    }

    public String getCardSecurityCode() {
        return cardSecurityCode;
    }

    public Transaction asEntity(Card card) {
        return new Transaction(amount, description, card);
    }
}
