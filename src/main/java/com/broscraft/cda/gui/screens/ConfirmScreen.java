package com.broscraft.cda.gui.screens;

import com.broscraft.cda.gui.utils.Styles;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;

import me.mattstudios.mfgui.gui.components.GuiAction;
import me.mattstudios.mfgui.gui.components.ItemBuilder;
import me.mattstudios.mfgui.gui.guis.Gui;

public class ConfirmScreen {
    protected Gui gui;

    public ConfirmScreen(String name, GuiAction<InventoryClickEvent> onConfirm, GuiAction<InventoryClickEvent> onCancel) {
        this.gui = new Gui(1, name);
        gui.setDefaultClickAction(event -> {
            event.setCancelled(true);
        });
        gui.setDragAction(event -> {
            event.setCancelled(true);
        });
        setIcons(onConfirm, onCancel);
    }

    private void setIcons(GuiAction<InventoryClickEvent> onConfirm, GuiAction<InventoryClickEvent> onCancel) {
        for (int i = 0; i < 9; ++i)
            this.gui.setItem(i, Styles.BACKGROUND);

        this.gui.setItem(2, ItemBuilder.from(Styles.CANCEL_ICON).asGuiItem(onCancel));
        this.gui.setItem(6, ItemBuilder.from(Styles.CONFIRM_ICON).asGuiItem(onConfirm));

    }

    public void open(HumanEntity player) {
        gui.open(player);
    }

}
