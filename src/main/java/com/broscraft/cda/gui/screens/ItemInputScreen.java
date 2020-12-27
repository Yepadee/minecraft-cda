package com.broscraft.cda.gui.screens;

import org.bukkit.entity.HumanEntity;

import me.mattstudios.mfgui.gui.guis.Gui;

public abstract class ItemInputScreen {
    protected Gui gui;

    public ItemInputScreen(int height, String name) {
        this.gui = new Gui(height, name);
    }

    public void open(HumanEntity player) {
        gui.open(player);
    }
}
