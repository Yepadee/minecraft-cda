// package com.broscraft.cda.gui.components;

// import com.github.stefvanschie.inventoryframework.gui.GuiItem;
// import com.github.stefvanschie.inventoryframework.pane.StaticPane;

// import org.bukkit.Material;
// import org.bukkit.event.inventory.InventoryClickEvent;
// import org.bukkit.inventory.ItemStack;

// public class Navbar {

//     public Navbar() {
//         super(1, 0, 7, 1);
//         this.addMyOrdersButton();
//         this.addSearchButton();
//     }

//     private void addMyOrdersButton() {
//         ItemStack searchIcon = new ItemStack(Material.WRITABLE_BOOK);
//         GuiItem searchButton = new GuiItem(searchIcon, this::onMyOrdersBtnClick);
//         this.addItem(searchButton, 2, 0);
//     }

//     private void addSearchButton() {
//         ItemStack searchIcon = new ItemStack(Material.COMPASS);
//         GuiItem searchButton = new GuiItem(searchIcon, this::onSearchBtnClick);
//         this.addItem(searchButton, 4, 0);
//     }

//     private void onMyOrdersBtnClick(InventoryClickEvent event) {
//         event.getWhoClicked().sendMessage("My Orders btn clicked!");
//     }

//     private void onSearchBtnClick(InventoryClickEvent event) {
//         event.getWhoClicked().sendMessage("Search btn clicked!");
//     }

// }
