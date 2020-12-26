package com.broscraft.cda.gui.screens.neworders;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.broscraft.cda.gui.screens.TextInputScreen;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.mattstudios.mfgui.gui.components.ItemBuilder;
import net.md_5.bungee.api.ChatColor;

public class PriceInputScreen extends TextInputScreen {
    private static ItemStack ENTER_ICON = ItemBuilder.from(Material.DIAMOND).setName("").build();
    private static String PLACEHOLDER = ChatColor.GRAY + "price";

    public PriceInputScreen(
        String name, BiConsumer<Player, String> onConfirmBtnClick, Consumer<Player> onClose
    ) {
        super(name, PLACEHOLDER, ENTER_ICON, onConfirmBtnClick, onClose);

    }
}
