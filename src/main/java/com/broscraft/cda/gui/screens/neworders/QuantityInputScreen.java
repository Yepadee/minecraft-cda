package com.broscraft.cda.gui.screens.neworders;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.broscraft.cda.gui.screens.TextInputScreen;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.mattstudios.mfgui.gui.components.ItemBuilder;
import net.md_5.bungee.api.ChatColor;

public class QuantityInputScreen extends TextInputScreen {
    private static ItemStack SEARCH_ICON = ItemBuilder.from(Material.COMPASS).setName("").build();
    private static String PLACEHOLDER = ChatColor.GRAY + "1";

    public QuantityInputScreen(
        int maxQuantity,
        BiConsumer<Player, String> onConfirmBtnClick,
        Consumer<Player> onClose
    ) {
        super(
            "Enter Quantitiy " + ChatColor.RED + ChatColor.BOLD + "(MAX: " + maxQuantity + ")",
            PLACEHOLDER,
            SEARCH_ICON,
            onConfirmBtnClick,
            onClose
        );

    }
}
