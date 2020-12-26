package com.broscraft.cda.gui.screens.neworders;

import com.broscraft.cda.model.items.ItemDTO;

import me.mattstudios.mfgui.gui.guis.Gui;

public class ItemInputScreen {

    protected static int HEIGHT = 6;

    private Gui gui;

    private ItemDTO item;

    public ItemInputScreen(String name, ItemDTO item) {
        this.gui = new Gui(HEIGHT, name);
    }

    private void setOnInsertItem(ItemDTO itemDTO) {
        // this.gui.set
    }
}
