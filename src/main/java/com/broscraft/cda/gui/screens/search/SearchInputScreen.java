package com.broscraft.cda.gui.screens.search;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.broscraft.cda.gui.screens.TextInputScreen;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.mattstudios.mfgui.gui.components.ItemBuilder;

public class SearchInputScreen extends TextInputScreen {
    private static ItemStack SEARCH_ICON = ItemBuilder.from(Material.COMPASS).setName("").build();

    public SearchInputScreen(
        BiConsumer<Player, String> onConfirmBtnClick, Consumer<Player> onClose
    ) {
        super("Search Item", SEARCH_ICON, onConfirmBtnClick, onClose);

    }

}
