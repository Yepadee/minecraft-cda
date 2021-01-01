package com.broscraft.cda.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.earth2me.essentials.api.Economy;
import com.earth2me.essentials.api.NoLoanPermittedException;
import com.earth2me.essentials.api.UserDoesNotExistException;

import org.bukkit.entity.HumanEntity;

public class EcoUtils {

    private static int CURRENCY_PRECISION = 2;

    public static BigDecimal parseMoney(String moneyStr) {
        return new BigDecimal(moneyStr).setScale(2, RoundingMode.HALF_EVEN);
    }

    public static boolean greaterThan(BigDecimal a, BigDecimal b) {
        return a.compareTo(b) > 0;
    }

    public static boolean lessThan(BigDecimal a, BigDecimal b) {
        return a.compareTo(b) < 0;
    }

    public static BigDecimal parseMoney(int moneyInt) {
        return BigDecimal.valueOf(moneyInt, CURRENCY_PRECISION).setScale(2, RoundingMode.HALF_EVEN);
    }

    public static int getValue(BigDecimal moneyBigDec) {
        int fractionalPart = moneyBigDec.remainder(BigDecimal.ONE).movePointRight(moneyBigDec.scale()).toBigInteger().intValue();
        int wholePart = moneyBigDec.toBigInteger().intValue() * (int) Math.pow(10, CURRENCY_PRECISION);
        return wholePart + fractionalPart;
    }


    public static BigDecimal multiply(BigDecimal price, int quantity) {
        return price.multiply(BigDecimal.valueOf(quantity));
    }
    
    public static String formatPriceCurrency(BigDecimal price) {
        return Economy.format(price);
    }

    public static void pay(HumanEntity player, BigDecimal price) {
        try {
            Economy.add(player.getUniqueId(), price);
        } catch (NoLoanPermittedException | ArithmeticException | UserDoesNotExistException e) {
            player.sendMessage("Failed to add funds! Please contact an admin.");
            e.printStackTrace();
        }
    }

    public static void charge(HumanEntity player, BigDecimal price) {
        try {
            Economy.subtract(player.getUniqueId(), price);
        } catch (NoLoanPermittedException | ArithmeticException | UserDoesNotExistException e) {
            player.sendMessage("Failed to subtract funds! Please contact an admin.");
            e.printStackTrace();
        }
    }

    public static boolean hasMoney(HumanEntity player, BigDecimal money) {
        try {
            return Economy.hasEnough(player.getUniqueId(), money);
        } catch (ArithmeticException e) {
            e.printStackTrace();
            return false;
        } catch (UserDoesNotExistException e) {
            e.printStackTrace();
            return false;
        }
    }

}
