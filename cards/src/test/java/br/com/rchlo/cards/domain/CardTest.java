package br.com.rchlo.cards.domain;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

public class CardTest {

    @Test
    public void shouldUpdateLimit() {
        var card = new Card();
        card.setAvailableLimit(new BigDecimal("1000"));
        card.updateLimit(new BigDecimal(500));
        var newLimit = card.getAvailableLimit();
        var expectedLimite = new BigDecimal("500");
        Assert.assertEquals(expectedLimite,newLimit);
    }
}