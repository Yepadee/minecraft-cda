package com.broscraft.cda.gui.screens.neworders;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.broscraft.cda.gui.screens.TextInputScreen;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.mattstudios.mfgui.gui.components.ItemBuilder;
import net.md_5.bungee.api.ChatColor;

public class NewOrderPriceInputScreen extends TextInputScreen {
    private static ItemStack CONFIRM_ICON = ItemBuilder.from(Material.PAPER).setName("").build();
    public NewOrderPriceInputScreen(
        String placeholder,
        BiConsumer<Player, String> onConfirmBtnClick,
        Consumer<Player> onClose
    ) {
        super(
            ChatColor.DARK_GREEN + "Enter a price",
            ChatColor.GRAY.toString() + placeholder,
            CONFIRM_ICON,
            onConfirmBtnClick,
            onClose
        );
    }
    
}