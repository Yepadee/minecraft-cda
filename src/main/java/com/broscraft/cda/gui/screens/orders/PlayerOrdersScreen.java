package com.broscraft.cda.gui.screens.orders;

import java.util.List;
import java.util.function.Consumer;

import com.broscraft.cda.gui.screens.ScrollableScreen;
import com.broscraft.cda.model.orders.OrderDTO;
import com.broscraft.cda.model.orders.OrderType;
import com.broscraft.cda.utils.ItemUtils;

import org.bukkit.inventory.ItemStack;

import me.mattstudios.mfgui.gui.components.ItemBuilder;
import me.mattstudios.mfgui.gui.components.ScrollType;
import net.md_5.bungee.api.ChatColor;

public class PlayerOrdersScreen extends ScrollableScreen {

    public PlayerOrdersScreen() {
        super("My Orders", ScrollType.VERTICAL);
    }

    private String getQuantityLore(OrderDTO orderDTO) {
        String quantityTxt = orderDTO.getQuantityFilled() + "/" +
        orderDTO.getQuantity();
        if (orderDTO.getType().equals(OrderType.BID)) {
            return ChatColor.AQUA +
            "Items recieved: " + quantityTxt;
        } else {
            return ChatColor.GOLD +
            "Items sold: " + quantityTxt;
        }
    }

    public void setOrders(List<OrderDTO> orders, Consumer<OrderDTO> onOrderClick) {
        orders.forEach(order -> {
            ItemStack orderIcon = ItemUtils.hideAttributes(
                ItemBuilder.from(ItemUtils.createIcon(order.getItem()))
                .setLore(
                    "OrderType: " + order.getType(),
                    getQuantityLore(order)
                )
                .build()
            );
            this.gui.addItem(ItemBuilder.from(orderIcon).asGuiItem());
        });
        this.gui.update();
    }

}
