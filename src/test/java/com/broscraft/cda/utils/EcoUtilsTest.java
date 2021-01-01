package com.broscraft.cda.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class EcoUtilsTest {

    @Test
    public void testMoneyParserCorrectDP()
    {
        System.out.println(EcoUtils.parseMoney("21.1"));
        assertEquals(EcoUtils.parseMoney(2110), EcoUtils.parseMoney("21.10"));
    }

    @Test
    public void testMoneyParserIncorrectDP()
    {
        System.out.println(EcoUtils.parseMoney("21.114"));
        assertEquals(EcoUtils.parseMoney(2111), EcoUtils.parseMoney("21.114"));
    }
}
