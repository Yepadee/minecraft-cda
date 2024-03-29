package com.broscraft.cda.gui.screens;

import java.util.function.Consumer;

import com.broscraft.cda.dtos.items.ItemDTO;
import com.broscraft.cda.gui.utils.Styles;
import com.broscraft.cda.utils.InventoryUtils;
import com.broscraft.cda.utils.ItemUtils;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import me.mattstudios.mfgui.gui.components.GuiAction;
import me.mattstudios.mfgui.gui.components.ItemBuilder;
import me.mattstudios.mfgui.gui.guis.Gui;
import me.mattstudios.mfgui.gui.guis.GuiItem;
import net.md_5.bungee.api.ChatColor;

public abstract class ItemInputScreen {
    private static int WIDTH = 9;
    private int height;
    protected Gui gui;

    private ItemDTO acceptedItem;

    public ItemInputScreen(int height, String name, ItemDTO acceptedItem) {
        this.gui = new Gui(height, name);
        this.height = height;
        this.gui.setDefaultClickAction(e -> {
            if (isOnBorder(e.getRawSlot())) e.setCancelled(true);
            else onItemClick(e);
        });
        this.acceptedItem = acceptedItem;

        setBorder();

        this.gui.setCloseGuiAction(e -> {
            dropAllItems(e.getPlayer());
        });
        
    }
    
    private void onItemClick(InventoryClickEvent e) {
        ItemStack item = e.getCurrentItem();
        if (item == null) return;
        ItemDTO clickedItem = ItemUtils.parseItemStack(item);
        if (!clickedItem.equals(acceptedItem)) e.setCancelled(true);
        if (ItemUtils.isDamaged(item)) {
            e.setCancelled(true);
            e.getWhoClicked().sendMessage(ChatColor.RED + "You cannot insert damaged items!");
        }
    }


    public void setBackBtn(GuiAction<InventoryClickEvent> onBack) {
        this.gui.setItem(0, ItemBuilder.from(Styles.BACK_ICON).asGuiItem(onBack));
    }

    protected void dropAllItems(HumanEntity player) {
        for (int row = 1; row < 5; ++ row) {
            for (int col = 1; col < 8; ++ col) {
                int slot = row * WIDTH + col;
                ItemStack item = this.gui.getInventory().getItem(slot);
                if (item != null) InventoryUtils.dropPlayerItems(player, item);
            }
        }
    }

    public void setConfirmBtn(Consumer<ItemStack> onConfirm) {
        this.gui.setItem(8, ItemBuilder.from(Styles.CONFIRM_ICON).asGuiItem(
            e -> {
                ItemStack insertedItems = getInsertedItems();
                clearItems();
                onConfirm.accept(insertedItems);
            }));
    }

    private boolean isOnBorder(int slot) {
        if (slot >= WIDTH * height) return false;
        else if (slot % WIDTH == 0 || (slot + 1) % WIDTH == 0) return true; // Sizes
        else if (slot < WIDTH || slot > (height - 1) * WIDTH) return true; // Top and Bottom
        else return false;
    }

    private void setBorder() {
        GuiItem background = Styles.BACKGROUND;
        for (int row = 1; row <= height; row ++) {
            gui.setItem(row, 1, background);
            gui.setItem(row, WIDTH, background);
        }
        for (int col = 1; col <= WIDTH; col ++) {
            gui.setItem(1, col, background);
            gui.setItem(height, col, background);
        }
    }

    protected ItemStack getInsertedItems() {
        int count = 0;
        ItemStack itemCopy = null;
        for (int row = 1; row < 5; ++ row) {
            for (int col = 1; col < 8; ++ col) {
                int slot = row * WIDTH + col;
                ItemStack item = this.gui.getInventory().getItem(slot);
                if (item != null) {
                    count += item.getAmount();
                    if (itemCopy == null) itemCopy = item.clone();
                }
            }
        }
        if (itemCopy != null) itemCopy.setAmount(count);
        return itemCopy;
    }

    protected void clearItems() {
        for (int row = 1; row < 5; ++ row) {
            for (int col = 1; col < 8; ++ col) {
                int slot = row * WIDTH + col;
                this.gui.getInventory().setItem(slot, null);
            }
        }   
    }

    public void open(HumanEntity player) {
        gui.open(player);
    }

    public void close(HumanEntity player) {
        this.gui.close(player);
    }
}
