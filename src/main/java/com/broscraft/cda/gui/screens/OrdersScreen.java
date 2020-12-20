package com.broscraft.cda.gui.screens;

import com.broscraft.cda.model.items.ItemDTO;
import com.broscraft.cda.model.items.visitors.ItemNameBuilder;
import com.broscraft.utils.ItemUitls;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;

import org.apache.commons.lang.WordUtils;

public class OrdersScreen extends ChestGui {
    public OrdersScreen(ItemDTO itemDto) {
        super(6, WordUtils.capitalize(ItemUitls.getItemName(itemDto)) + " Orders");
    }
}
