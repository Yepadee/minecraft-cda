package com.broscraft.cda.gui.screens.item;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.broscraft.cda.dtos.orders.grouped.GroupedAskDTO;
import com.broscraft.cda.dtos.orders.grouped.GroupedBidDTO;
import com.broscraft.cda.dtos.orders.grouped.GroupedOrderDTO;
import com.broscraft.cda.dtos.orders.grouped.GroupedOrdersDTO;
import com.broscraft.cda.gui.screens.ScrollableScreen;
import com.broscraft.cda.gui.utils.Styles;
import com.broscraft.cda.utils.ItemUtils;

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
        this.setCreateOrderButtons(onNewBidClick, onNewAskClick);
    }

    private void setCreateOrderButtons(
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


    public void setOrders(
        GroupedOrdersDTO groupedOrders,
        Function<GroupedOrderDTO, GuiAction<InventoryClickEvent>> onBidClick,
        Function<GroupedOrderDTO, GuiAction<InventoryClickEvent>> onAskClick
    ) {
        // This is a very aids way of doing the UI, but necessary give the lack of "Panes" in this gui framework :(
        List<GroupedBidDTO> groupedBids = groupedOrders.getGroupedBids();
        List<GroupedAskDTO> groupedAsks = groupedOrders.getGroupedAsks();
        int numBids = groupedBids.size();
        int numAsks = groupedAsks.size();
        if (numBids > 0 || numAsks > 0) {
            int smallest = numBids < numAsks ? numBids : numAsks;

            List<GuiItem> icons = new ArrayList<>();
    
            if (smallest > 0) {
                icons.add(Styles.BEST_ORDER_ICON);
                GroupedBidDTO groupedBid = groupedBids.get(0);
                GroupedAskDTO groupedAsk = groupedAsks.get(0);
                icons.add(ItemBuilder.from(ItemUtils.createGroupedOrderIcon(groupedBid)).glow(true).asGuiItem(onBidClick.apply(groupedBid)));
                icons.add(ItemBuilder.from(ItemUtils.createGroupedOrderIcon(groupedAsk)).glow(true).asGuiItem(onAskClick.apply(groupedAsk)));
            } else {
                if (numAsks == 0) {
                    GroupedBidDTO groupedBid = groupedBids.get(0);
                    icons.add(Styles.BEST_ORDER_ICON);
                    icons.add(ItemBuilder.from(ItemUtils.createGroupedOrderIcon(groupedBid)).glow(true).asGuiItem(onBidClick.apply(groupedBid)));
                    icons.add(Styles.NO_ITEM_ORDER_BTN);
                    
                } else if (numBids == 0) {
                    GroupedAskDTO groupedAsk = groupedAsks.get(0);
                    icons.add(Styles.BEST_ORDER_ICON);
                    icons.add(Styles.NO_ITEM_ORDER_BTN);
                    icons.add(ItemBuilder.from(ItemUtils.createGroupedOrderIcon(groupedAsk)).glow(true).asGuiItem(onAskClick.apply(groupedAsk)));
                }
            }
    
    
            for (int i = 1; i < smallest; ++i) {
                GroupedBidDTO groupedBid = groupedBids.get(i);
                GroupedAskDTO groupedAsk = groupedAsks.get(i);
                icons.add(Styles.OTHER_ORDER_ICON);
                icons.add(ItemBuilder.from(ItemUtils.createGroupedOrderIcon(groupedBid)).glow(i % 2 == 0).asGuiItem(onBidClick.apply(groupedBid)));
                icons.add(ItemBuilder.from(ItemUtils.createGroupedOrderIcon(groupedAsk)).glow(i % 2 == 0).asGuiItem(onAskClick.apply(groupedAsk)));
            }
            if (smallest == numBids) {
                if (smallest == 0) smallest ++;
                for (int i = smallest; i < numAsks; ++i) {
                    GroupedAskDTO groupedAsk = groupedAsks.get(i);
                    icons.add(Styles.OTHER_ORDER_ICON);
                    icons.add(Styles.NO_ITEM_ORDER_BTN);
                    icons.add(ItemBuilder.from(ItemUtils.createGroupedOrderIcon(groupedAsk)).glow(i % 2 == 0).asGuiItem(onAskClick.apply(groupedAsk)));
                }
            } else {
                if (smallest == 0) smallest ++;
                for (int i = smallest; i < numBids; ++i) {
                    GroupedBidDTO groupedBid = groupedBids.get(i);
                    icons.add(Styles.OTHER_ORDER_ICON);
                    icons.add(ItemBuilder.from(ItemUtils.createGroupedOrderIcon(groupedBid)).glow(i % 2 == 0).asGuiItem(onBidClick.apply(groupedBid)));
                    icons.add(Styles.NO_ITEM_ORDER_BTN);
                }
            }
            for (int i = 0; i < 3; ++i) {
                icons.add(Styles.END_OF_ORDERS);
            }
    
            this.setItems(icons);
    
            update();
        }
        
    }
}
