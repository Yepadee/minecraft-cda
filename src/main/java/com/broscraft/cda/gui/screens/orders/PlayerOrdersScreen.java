package com.broscraft.cda.gui.screens.orders;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.broscraft.cda.gui.screens.ScrollableScreen;
import com.broscraft.cda.gui.utils.Styles;
import com.broscraft.cda.model.orders.OrderDTO;
import com.broscraft.cda.model.orders.OrderType;
import com.broscraft.cda.observers.OrderUpdateObserver;
import com.broscraft.cda.utils.ItemUtils;
import com.google.common.base.Function;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.mattstudios.mfgui.gui.components.GuiAction;
import me.mattstudios.mfgui.gui.components.ItemBuilder;
import me.mattstudios.mfgui.gui.components.ScrollType;
import net.md_5.bungee.api.ChatColor;

public class PlayerOrdersScreen extends ScrollableScreen implements OrderUpdateObserver {
    Map<Long, ItemStack> orderBtns = new HashMap<>();

    public PlayerOrdersScreen(GuiAction<InventoryClickEvent> onBackBtnClick) {
        super("My Orders", ScrollType.VERTICAL);
        createNavbar(onBackBtnClick);

    }

    private void createNavbar(GuiAction<InventoryClickEvent> onBackBtnClick) {
        this.gui.setItem(1, 1, ItemBuilder.from(Styles.BACK_ICON).asGuiItem(onBackBtnClick));
    }


    private List<String> getLore(OrderDTO orderDTO) {
        String quantityTxt;
        String orderTypeTxt;
        String collectTxt;

        ChatColor color;

        if (orderDTO.getType().equals(OrderType.BID)) {
            color = ChatColor.GOLD;
            quantityTxt = ChatColor.GRAY + "Recieved: ";
            orderTypeTxt = color + ChatColor.UNDERLINE.toString() + "         Bid         ";
            collectTxt = ChatColor.GRAY + "Collect Items: " + ChatColor.GREEN + orderDTO.getToCollect();
        } else {
            color = ChatColor.AQUA;
            quantityTxt = ChatColor.GRAY + "Sold: ";
            orderTypeTxt = color + ChatColor.UNDERLINE.toString() + "         Ask         ";
            collectTxt = ChatColor.GRAY + "Collect Money: " + ChatColor.GREEN + Styles.formatPrice(orderDTO.getToCollect() * orderDTO.getPrice());
        }

        quantityTxt += color.toString() +
            orderDTO.getQuantityFilled() + "/" +
            orderDTO.getQuantity();

        return Arrays.asList(
            orderTypeTxt,
            collectTxt,
            quantityTxt,
            ChatColor.GRAY + "Price: " + color.toString() + Styles.formatPrice(orderDTO.getPrice()),
            ChatColor.GRAY + ChatColor.UNDERLINE.toString() + "                      ",
            ChatColor.DARK_GREEN + "Left click: Collect",
            ChatColor.DARK_RED + "Right click: Delete"
        );
    }

    private void updateOrderIcon(ItemStack orderIcon, OrderDTO orderDTO) {
        ItemMeta meta = orderIcon.getItemMeta();
        meta.setLore(getLore(orderDTO));
        orderIcon.setItemMeta(meta);
    }

    public void setOrders(List<OrderDTO> orders, Function<OrderDTO, GuiAction<InventoryClickEvent>> onOrderClick) {
        orders.forEach(order -> {
            ItemStack orderIcon = ItemUtils.hideAttributes(
                ItemBuilder.from(ItemUtils.createIcon(order.getItem()))
                .setLore(this.getLore(order))
                .build()
            );
            this.orderBtns.put(order.getId(), orderIcon);
            this.gui.addItem(ItemBuilder.from(orderIcon).asGuiItem(onOrderClick.apply(order)));
        });
        this.gui.update();
    }

    @Override
    public void onOrderUpdate(OrderDTO orderDTO) {
        ItemStack orderIcon = Objects.requireNonNull(orderBtns.get(orderDTO.getId()));
        updateOrderIcon(orderIcon, orderDTO);

    }

}
