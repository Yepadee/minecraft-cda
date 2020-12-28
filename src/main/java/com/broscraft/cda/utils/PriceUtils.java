package com.broscraft.cda.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.UUID;

import com.earth2me.essentials.api.Economy;
import com.earth2me.essentials.api.NoLoanPermittedException;
import com.earth2me.essentials.api.UserDoesNotExistException;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

public class PriceUtils {
    public static NumberFormat priceFormat = NumberFormat.getInstance(Locale.UK);
    static {
        priceFormat.setMaximumFractionDigits(2);
        priceFormat.setMinimumFractionDigits(2);
        priceFormat.setRoundingMode(RoundingMode.HALF_UP);
    }

    public static float formatPrice(float price) {
        return Float.parseFloat(priceFormat.format(price));
    }

    public static float formatPrice(String price) {
        return Float.parseFloat(priceFormat.format(Float.parseFloat(price)));
    }

    public static String formatPriceCurrency(float price) {
        return Economy.format(BigDecimal.valueOf(price));
    }

    public static void pay(UUID playerUUID, float price) {
        try {
            Economy.add(playerUUID, BigDecimal.valueOf(price));
        } catch (NoLoanPermittedException | ArithmeticException | UserDoesNotExistException e) {
            Bukkit.getPlayer(playerUUID).sendMessage("Failed to add funds! Please contact an admin.");
            e.printStackTrace();
        }
    }

    public static void pay(HumanEntity player, float price) {
        pay(player.getUniqueId(), price);
    }

    public static void charge(UUID playerUUID, float price) {
        try {
            Economy.subtract(playerUUID, BigDecimal.valueOf(price));
        } catch (NoLoanPermittedException | ArithmeticException | UserDoesNotExistException e) {
            Bukkit.getPlayer(playerUUID).sendMessage("Failed to subtract funds! Please contact an admin.");
            e.printStackTrace();
        }
    }

    public static void charge(Player player, float price) {
        charge(player.getUniqueId(), price);
    }

}
