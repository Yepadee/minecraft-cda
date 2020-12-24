package com.broscraft.cda.gui.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import me.mattstudios.mfgui.gui.components.ItemBuilder;
import me.mattstudios.mfgui.gui.guis.GuiItem;
import net.md_5.bungee.api.ChatColor;

public class Styles {
    public static GuiItem BACKGROUND = ItemBuilder.from(Material.WHITE_STAINED_GLASS_PANE).setName(ChatColor.BLACK + "_").asGuiItem();
    public static GuiItem BACKGROUND_DARK = ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE).setName(ChatColor.BLACK + "_").asGuiItem();
    public static ItemStack BACK_ICON = ItemBuilder.from(Material.BARRIER).setName("Back").build();
    public static ItemStack CLOSE_ICON = ItemBuilder.from(Material.BARRIER).setName("Close").build();

    public static ItemStack SEARCH_ICON = ItemBuilder.from(Material.COMPASS).setName("Search").build();
    public static ItemStack MY_ORDERS_ICON = ItemBuilder.from(Material.WRITABLE_BOOK).setName("My Orders").build();
}
