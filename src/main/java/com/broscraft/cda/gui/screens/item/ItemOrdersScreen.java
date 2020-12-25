package com.broscraft.cda.gui.screens.item;

import java.util.ArrayList;
import java.util.List;

import com.broscraft.cda.gui.screens.ScrollableScreen;
import com.broscraft.cda.gui.utils.Styles;
import com.broscraft.cda.model.orders.grouped.GroupedAskDTO;
import com.broscraft.cda.model.orders.grouped.GroupedBidDTO;
import com.broscraft.cda.model.orders.grouped.GroupedOrdersDTO;
import com.broscraft.cda.utils.ItemUtils;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import me.mattstudios.mfgui.gui.components.GuiAction;
import me.mattstudios.mfgui.gui.components.ItemBuilder;
import me.mattstudios.mfgui.gui.components.ScrollType;
import me.mattstudios.mfgui.gui.guis.GuiItem;

public class ItemOrdersScreen extends ScrollableScreen {

    public ItemOrdersScreen(
        ItemStack item,
        GuiAction<InventoryClickEvent> onBackBtnClick,
        GuiAction<InventoryClickEvent> onNewBidClick,
        GuiAction<InventoryClickEvent> onNewAskClick
    ) {
        super("Item Orders", ScrollType.HORIZONTAL, 7, 3);
        this.createNavbar(item, onBackBtnClick);
        this.setGuiIcons();
        this.setCreateOrderButtons(ItemUtils.getId(item), onNewBidClick, onNewAskClick);
    }

    private void setCreateOrderButtons(
        Long itemId,
        GuiAction<InventoryClickEvent> onNewBidClick,
        GuiAction<InventoryClickEvent> onNewAskClick
    ) {
        GuiItem newBidBtn = ItemBuilder.from(Styles.NEW_BID_ICON).asGuiItem(onNewBidClick);
        GuiItem newAskBtn = ItemBuilder.from(Styles.NEW_ASK_ICON).asGuiItem(onNewAskClick);

        gui.setItem(3, WIDTH, newBidBtn);
        gui.setItem(4, WIDTH, newAskBtn);
    }

    private void setGuiIcons() {
        GuiItem background = Styles.BACKGROUND_DARK;
        gui.setItem(2, 1, background);
        gui.setItem(2, WIDTH, background);
        for (int col = 1; col <= WIDTH; col ++) {
            gui.setItem(5, col, background);
        }

        gui.setItem(3, 1, Styles.BID_ORDERS_ICON);
        gui.setItem(4, 1, Styles.ASK_ORDERS_ICON);

    }

    private void createNavbar(ItemStack item, GuiAction<InventoryClickEvent> onBackBtnClick) {
        this.gui.setItem(1, 1, ItemBuilder.from(Styles.BACK_ICON).asGuiItem(onBackBtnClick));
        this.gui.setItem(1, 5, ItemBuilder.from(item).asGuiItem());
    }


    public void setOrders(GroupedOrdersDTO groupedOrders) {
        // This is a very aids way of doing the UI, but necessary give the lack of "Panes" in this gui framework :(
        List<GroupedBidDTO> groupedBids = groupedOrders.getGroupedBids();
        List<GroupedAskDTO> groupedAsks = groupedOrders.getGroupedAsks();
        int numBids = groupedBids.size();
        int numAsks = groupedAsks.size();
        int smallest = numBids < numAsks ? numBids : numAsks;

        List<GuiItem> icons = new ArrayList<>();

        if (smallest > 0) {
            icons.add(Styles.BEST_ORDER_ICON);
            GroupedBidDTO groupedBid = groupedBids.get(0);
            GroupedAskDTO groupedAsk = groupedAsks.get(0);
            icons.add(ItemBuilder.from(ItemUtils.createGroupedOrderIcon(groupedBid)).glow(true).asGuiItem());
            icons.add(ItemBuilder.from(ItemUtils.createGroupedOrderIcon(groupedAsk)).glow(true).asGuiItem());
        } else {
            if (numAsks == 0) {
                GroupedBidDTO groupedBid = groupedBids.get(0);
                icons.add(Styles.BEST_ORDER_ICON);
                icons.add(ItemBuilder.from(ItemUtils.createGroupedOrderIcon(groupedBid)).glow(true).asGuiItem());
                icons.add(ItemBuilder.from(Material.BARRIER).asGuiItem());
                
            } else if (numBids == 0) {
                GroupedAskDTO groupedAsk = groupedAsks.get(0);
                icons.add(Styles.BEST_ORDER_ICON);
                icons.add(ItemBuilder.from(Material.BARRIER).asGuiItem());
                icons.add(ItemBuilder.from(ItemUtils.createGroupedOrderIcon(groupedAsk)).glow(true).asGuiItem());
            }
        }


        for (int i = 1; i < smallest; ++i) {
            GroupedBidDTO groupedBid = groupedBids.get(i);
            GroupedAskDTO groupedAsk = groupedAsks.get(i);
            icons.add(Styles.OTHER_ORDER_ICON);
            icons.add(ItemBuilder.from(ItemUtils.createGroupedOrderIcon(groupedBid)).glow(i % 2 == 0).asGuiItem());
            icons.add(ItemBuilder.from(ItemUtils.createGroupedOrderIcon(groupedAsk)).glow(i % 2 == 0).asGuiItem());
        }
        if (smallest == numBids) {
            if (smallest == 0) smallest ++;
            for (int i = smallest; i < numAsks; ++i) {
                GroupedAskDTO groupedAsk = groupedAsks.get(i);
                icons.add(Styles.OTHER_ORDER_ICON);
                icons.add(ItemBuilder.from(Material.BARRIER).asGuiItem());
                icons.add(ItemBuilder.from(ItemUtils.createGroupedOrderIcon(groupedAsk)).glow(i % 2 == 0).asGuiItem());
            }
        } else {
            if (smallest == 0) smallest ++;
            for (int i = smallest; i < numBids; ++i) {
                GroupedBidDTO groupedBid = groupedBids.get(i);
                icons.add(Styles.OTHER_ORDER_ICON);
                icons.add(ItemBuilder.from(ItemUtils.createGroupedOrderIcon(groupedBid)).glow(i % 2 == 0).asGuiItem());
                icons.add(ItemBuilder.from(Material.BARRIER).asGuiItem());
            }
        }
        for (int i = 0; i < 3; ++i) {
            icons.add(ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).asGuiItem());
        }

        this.setItems(icons);

        update();
    }
}
