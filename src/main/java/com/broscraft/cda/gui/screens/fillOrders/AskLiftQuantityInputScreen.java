package com.broscraft.cda.gui.screens.fillOrders;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.broscraft.cda.gui.screens.TextInputScreen;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.mattstudios.mfgui.gui.components.ItemBuilder;
import net.md_5.bungee.api.ChatColor;

public class AskLiftQuantityInputScreen extends TextInputScreen {
    private static ItemStack CONFIRM_ICON = ItemBuilder.from(Material.PAPER).setName("").build();
    private static String PLACEHOLDER = ChatColor.GRAY + "_";

    public AskLiftQuantityInputScreen(
        BiConsumer<Player, String> onConfirmBtnClick, Consumer<Player> onClose
    ) {
        super(
            ChatColor.BOLD + ChatColor.AQUA.toString() + "Enter quantity to buy",
            PLACEHOLDER,
            CONFIRM_ICON,
            onConfirmBtnClick,
            onClose
        );
    }
    
}
