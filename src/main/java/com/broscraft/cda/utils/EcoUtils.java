package com.broscraft.cda.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

import com.earth2me.essentials.api.Economy;
import com.earth2me.essentials.api.NoLoanPermittedException;
import com.earth2me.essentials.api.UserDoesNotExistException;

import org.bukkit.entity.HumanEntity;

public class EcoUtils {
    public static NumberFormat priceFormat = NumberFormat.getInstance(Locale.UK);
    static {
        priceFormat.setMaximumFractionDigits(2);
        priceFormat.setMinimumFractionDigits(2);
        priceFormat.setRoundingMode(RoundingMode.HALF_UP);
    }
    
    public static String formatPriceCurrency(float price) {
        return Economy.format(BigDecimal.valueOf(price));
    }

    public static void pay(HumanEntity player, float price) {
        try {
            Economy.add(player.getUniqueId(), BigDecimal.valueOf(price));
        } catch (NoLoanPermittedException | ArithmeticException | UserDoesNotExistException e) {
            player.sendMessage("Failed to add funds! Please contact an admin.");
            e.printStackTrace();
        }
    }

    public static void charge(HumanEntity player, float price) {
        try {
            Economy.subtract(player.getUniqueId(), BigDecimal.valueOf(price));
        } catch (NoLoanPermittedException | ArithmeticException | UserDoesNotExistException e) {
            player.sendMessage("Failed to subtract funds! Please contact an admin.");
            e.printStackTrace();
        }
    }

    public static boolean hasMoney(HumanEntity player, float money) {
        try {
            return Economy.hasEnough(player.getUniqueId(), BigDecimal.valueOf(money));
        } catch (ArithmeticException e) {
            e.printStackTrace();
            return false;
        } catch (UserDoesNotExistException e) {
            e.printStackTrace();
            return false;
        }
    }

}
