package com.broscraft.cda.gui.screens.orders;

import java.util.List;
import com.broscraft.cda.gui.screens.ScrollableScreen;
import com.broscraft.cda.model.orders.OrderDTO;
import com.broscraft.cda.model.orders.OrderType;
import com.broscraft.cda.utils.ItemUtils;
import com.google.common.base.Function;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import me.mattstudios.mfgui.gui.components.GuiAction;
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
            "Recieved: " + quantityTxt;
        } else {
            return ChatColor.GOLD +
            "Sold: " + quantityTxt;
        }
    }

    public void setOrders(List<OrderDTO> orders, Function<OrderDTO, GuiAction<InventoryClickEvent>> onOrderClick) {
        orders.forEach(order -> {
            ItemStack orderIcon = ItemUtils.hideAttributes(
                ItemBuilder.from(ItemUtils.createIcon(order.getItem()))
                .setLore(
                    ChatColor.GRAY + ChatColor.UNDERLINE.toString() + "                      ",
                    "OrderType: " + order.getType(),
                    ChatColor.YELLOW + "Price: " + order.getPrice(),
                    getQuantityLore(order),
                    ChatColor.GRAY + ChatColor.UNDERLINE.toString() + "                      ",
                    ChatColor.GREEN + "Left click: Collect",
                    ChatColor.RED + "Right click: Delete"
                )
                .build()
            );
            this.gui.addItem(ItemBuilder.from(orderIcon).asGuiItem(onOrderClick.apply(order)));
        });
        this.gui.update();
    }

}
