package com.broscraft.cda.gui.screens.orders;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.broscraft.cda.dtos.orders.OrderDTO;
import com.broscraft.cda.dtos.orders.OrderType;
import com.broscraft.cda.gui.screens.ScrollableScreen;
import com.broscraft.cda.gui.utils.Styles;
import com.broscraft.cda.observers.OrderUpdateObserver;
import com.broscraft.cda.utils.ItemUtils;
import com.broscraft.cda.utils.EcoUtils;
import com.google.common.base.Function;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.mattstudios.mfgui.gui.components.GuiAction;
import me.mattstudios.mfgui.gui.components.ItemBuilder;
import me.mattstudios.mfgui.gui.components.ScrollType;
import me.mattstudios.mfgui.gui.guis.GuiItem;
import net.md_5.bungee.api.ChatColor;

public class PlayerOrdersScreen extends ScrollableScreen implements OrderUpdateObserver {
    Map<Long, GuiItem> orderBtns = new HashMap<>();

    public PlayerOrdersScreen(GuiAction<InventoryClickEvent> onBackBtnClick) {
        super("My Orders", ScrollType.VERTICAL);
        createNavbar(onBackBtnClick);

    }

    private void createNavbar(GuiAction<InventoryClickEvent> onBackBtnClick) {
        this.gui.setItem(1, 1, ItemBuilder.from(Styles.BACK_ICON).asGuiItem(onBackBtnClick));
        this.gui.setItem(1, 5, ItemBuilder.from(Styles.MY_ORDERS_ICON).asGuiItem());
    }


    private List<String> getLore(OrderDTO orderDTO) {
        String quantityTxt;
        String orderTypeTxt;
        String collectTxt;

        ChatColor color;
        ChatColor toCollectColor = ChatColor.GREEN;
        if (orderDTO.getToCollect() == 0) toCollectColor = ChatColor.RED;

        if (orderDTO.getType().equals(OrderType.BID)) {
            color = ChatColor.GOLD;
            quantityTxt = ChatColor.GRAY + "Recieved: ";
            orderTypeTxt = color + ChatColor.UNDERLINE.toString() + "         Bid         ";
            collectTxt = ChatColor.GRAY + "Collect Items: " + toCollectColor + orderDTO.getToCollect();
        } else {
            color = ChatColor.AQUA;
            quantityTxt = ChatColor.GRAY + "Sold: ";
            orderTypeTxt = color + ChatColor.UNDERLINE.toString() + "         Ask         ";
            collectTxt = ChatColor.GRAY + "Collect Money: " + toCollectColor + EcoUtils.formatPriceCurrency(orderDTO.getToCollect() * orderDTO.getPrice());
        }

        quantityTxt += color.toString() +
            orderDTO.getQuantityFilled() + "/" +
            orderDTO.getQuantity();

        return Arrays.asList(
            orderTypeTxt,
            collectTxt,
            quantityTxt,
            ChatColor.GRAY + "Price: " + color.toString() + EcoUtils.formatPriceCurrency(orderDTO.getPrice()),
            ChatColor.GRAY + ChatColor.UNDERLINE.toString() + "                      ",
            ChatColor.DARK_GREEN + "Left click: Collect",
            ChatColor.DARK_RED + "Right click: Delete"
        );
    }

    private void updateOrderIcon(GuiItem orderBtn, OrderDTO orderDTO) {
        ItemStack orderIcon = orderBtn.getItemStack();
        ItemMeta meta = orderIcon.getItemMeta();
        meta.setLore(getLore(orderDTO));
        orderIcon.setItemMeta(meta);
        orderBtn.setItemStack(orderIcon);
        this.update();
    }

    public void setOrders(List<OrderDTO> orders, Function<OrderDTO, GuiAction<InventoryClickEvent>> onOrderClick) {
        orders.forEach(order -> {
            ItemStack orderIcon = ItemUtils.hideAttributes(
                ItemBuilder.from(ItemUtils.buildItemStack(order.getItem()))
                .setLore(this.getLore(order))
                .build()
            );

            GuiItem orderBtn = ItemBuilder.from(orderIcon).asGuiItem(onOrderClick.apply(order));
            this.orderBtns.put(order.getId(), orderBtn);
            this.gui.addItem(orderBtn);
        });
        this.gui.update();
    }

    @Override
    public void onOrderUpdate(OrderDTO orderDTO) {
        GuiItem orderBtn = Objects.requireNonNull(orderBtns.get(orderDTO.getId()));
        updateOrderIcon(orderBtn, orderDTO);

    }

}
