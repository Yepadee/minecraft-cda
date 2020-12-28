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
    private static String PLACEHOLDER = ChatColor.GRAY + "_";
    public NewOrderPriceInputScreen(
        BiConsumer<Player, String> onConfirmBtnClick,
        Consumer<Player> onClose
    ) {
        super(
            "Enter a price:",
            PLACEHOLDER,
            CONFIRM_ICON,
            onConfirmBtnClick,
            onClose
        );
    }
    
}