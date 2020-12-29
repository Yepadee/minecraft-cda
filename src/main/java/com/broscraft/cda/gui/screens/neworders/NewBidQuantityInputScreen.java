package com.broscraft.cda.gui.screens.neworders;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.broscraft.cda.gui.screens.TextInputScreen;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.mattstudios.mfgui.gui.components.ItemBuilder;
import net.md_5.bungee.api.ChatColor;

public class NewBidQuantityInputScreen extends TextInputScreen {
    private static ItemStack CONFIRM_ICON = ItemBuilder.from(Material.GOLD_INGOT).setName("").build();
    private static String PLACEHOLDER = ChatColor.GRAY + "1_";
    public NewBidQuantityInputScreen(
        BiConsumer<Player, String> onConfirmBtnClick,
        Consumer<Player> onClose
    ) {
        super(
            "Enter a quantity:",
            PLACEHOLDER,
            CONFIRM_ICON,
            onConfirmBtnClick,
            onClose
        );
    }
    
}
