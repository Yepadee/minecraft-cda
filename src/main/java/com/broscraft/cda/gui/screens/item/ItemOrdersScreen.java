package com.broscraft.cda.gui.screens.item;

import java.util.function.Function;

import com.broscraft.cda.gui.screens.ScrollableScreen;
import com.broscraft.cda.utils.ItemUitls;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import me.mattstudios.mfgui.gui.components.GuiAction;
import me.mattstudios.mfgui.gui.components.ItemBuilder;
import me.mattstudios.mfgui.gui.components.ScrollType;
import me.mattstudios.mfgui.gui.guis.GuiItem;

public class ItemOrdersScreen extends ScrollableScreen {

    private static Material BID_ORDERS_MATERIAL = Material.GOLD_INGOT;
    private static Material ASK_ORDERS_MATERIAL = Material.IRON_INGOT;

    private static Material NEW_ORDER_MATERIAL = Material.PAPER;

    private static Material BEST_ORDER_MATERIAL = Material.GREEN_STAINED_GLASS_PANE;
    private static Material OTHER_ORDER_MATERIAL = Material.RED_STAINED_GLASS_PANE;

    private static Material ORDERS_BACKGROUND_MATERIAL = Material.GRAY_STAINED_GLASS_PANE;


    public ItemOrdersScreen(
        ItemStack item,
        GuiAction<InventoryClickEvent> onBackBtnClick,
        Function<Long, GuiAction<InventoryClickEvent>> onNewBidClick,
        Function<Long, GuiAction<InventoryClickEvent>> onNewAskClick
    ) {
        super("Item Orders", ScrollType.HORIZONTAL);
        this.createNavbar(item, onBackBtnClick);
        this.setGuiIcons();
        this.setCreateOrderButtons(ItemUitls.getId(item), onNewBidClick, onNewAskClick);
    }

    private void setCreateOrderButtons(
        Long itemId,
        Function<Long, GuiAction<InventoryClickEvent>> onNewBidClick,
        Function<Long, GuiAction<InventoryClickEvent>> onNewAskClick
    ) {
        GuiItem newBidBtn = ItemBuilder.from(NEW_ORDER_MATERIAL).setName("New Bid").setLore("Creates a new bid").asGuiItem(onNewBidClick.apply(itemId));
        GuiItem newAskBtn = ItemBuilder.from(NEW_ORDER_MATERIAL).setName("New Ask").setLore("Creates a new ask").asGuiItem(onNewAskClick.apply(itemId));

        gui.setItem(3, WIDTH, newBidBtn);
        gui.setItem(4, WIDTH, newAskBtn);
    }

    private void setGuiIcons() {
        GuiItem background = ItemBuilder.from(ORDERS_BACKGROUND_MATERIAL).setName("").asGuiItem();
        gui.setItem(2, 1, background);
        gui.setItem(2, WIDTH, background);
        for (int col = 1; col <= WIDTH; col ++) {
            gui.setItem(5, col, background);
        }

        GuiItem bestOrderIcon = ItemBuilder.from(BEST_ORDER_MATERIAL).setName("Best Price").asGuiItem();
        GuiItem otherOrderIcon = ItemBuilder.from(OTHER_ORDER_MATERIAL).setName("Other Prices").asGuiItem();

        gui.setItem(2, 2, bestOrderIcon);

        for (int col = 3; col < WIDTH; col ++) {
            gui.setItem(2, col, otherOrderIcon);
        }
        
        GuiItem bidOrdersIcon = ItemBuilder.from(BID_ORDERS_MATERIAL).setName("Bids").asGuiItem();
        GuiItem askOrdersIcon = ItemBuilder.from(ASK_ORDERS_MATERIAL).setName("Asks").asGuiItem();

        gui.setItem(3, 1, bidOrdersIcon);
        gui.setItem(4, 1, askOrdersIcon);

    }

    private void createNavbar(ItemStack item, GuiAction<InventoryClickEvent> onBackBtnClick) {
        this.gui.setItem(1, 1, ItemBuilder.from(Material.BARRIER).setName("Back").asGuiItem(onBackBtnClick));
        this.gui.setItem(1, 5, ItemBuilder.from(item).asGuiItem());
    }

}