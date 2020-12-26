package com.broscraft.cda.gui.screens;

import java.util.List;

import com.broscraft.cda.gui.utils.Styles;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryCloseEvent;

import me.mattstudios.mfgui.gui.components.GuiAction;
import me.mattstudios.mfgui.gui.components.ItemBuilder;
import me.mattstudios.mfgui.gui.components.ScrollType;
import me.mattstudios.mfgui.gui.guis.GuiItem;
import me.mattstudios.mfgui.gui.guis.ScrollingGui;

public abstract class ScrollableScreen {
    protected ScrollingGui gui;

    protected static int WIDTH = 9;
    protected static int HEIGHT = 6;

    private int itemWindowWidth, itemWindowHeight;
    private int itemWindowSize;

    private int prevBtnRow = HEIGHT,
                prevBtnCol = 1,
                nextBtnRow = HEIGHT,
                nextBtnCol = WIDTH;

    private int currentScrollNotch = 0;
    private int numScrollNotches = 0;
    private int scrollNotchSize = 0;
    private int numItems = 0;


    public ScrollableScreen(String name, ScrollType scrollType) {
        this(name, scrollType, 7, 4);
    }

    public ScrollableScreen(String name, ScrollType scrollType, int itemWindowWidth, int itemWindowHeight) {
        this.itemWindowWidth = itemWindowWidth;
        this.itemWindowHeight = itemWindowHeight;
        this.itemWindowSize = itemWindowWidth * itemWindowHeight;
        this.gui = new ScrollingGui(HEIGHT, name, scrollType);
        gui.setDefaultClickAction(event -> {
            event.setCancelled(true);
        });
        gui.setDragAction(event -> {
            event.setCancelled(true);
        });

        this.setBorder();
        this.addNavigationButtons(scrollType);
    }

    private void setBorder() {
        GuiItem background = Styles.BACKGROUND;
        for (int row = 1; row <= HEIGHT; row ++) {
            gui.setItem(row, 1, background);
            gui.setItem(row, WIDTH, background);
        }
        for (int col = 1; col <= WIDTH; col ++) {
            gui.setItem(1, col, background);
            gui.setItem(HEIGHT, col, background);
        }
    }

    private void addNavigationButtons(ScrollType scrollType) {
        if (scrollType.equals(ScrollType.VERTICAL)) {
            prevBtnRow = 1;
            prevBtnCol = WIDTH;
            scrollNotchSize = itemWindowWidth;
        } else {
            scrollNotchSize = itemWindowHeight;
        }

    }

    private GuiItem getPrevBtn() {
        return ItemBuilder.from(Styles.PREV_ICON).asGuiItem(e -> onPrevBtnClick());
    }

    private GuiItem getNextBtn() {
        return ItemBuilder.from(Styles.NEXT_ICON).asGuiItem(e -> onNextBtnClick()); // using this::function wont pick up on changes in class instance
    }

    private void hidePrevBtn() {
        this.gui.updateItem(prevBtnRow, prevBtnCol, Styles.BACKGROUND);
    }

    private void showPrevBtn() {
        this.gui.updateItem(prevBtnRow, prevBtnCol, getPrevBtn());
    }

    private void hideNextBtn() {
        this.gui.updateItem(nextBtnRow, nextBtnCol, Styles.BACKGROUND);
    }

    private void showNextBtn() {
        this.gui.updateItem(nextBtnRow, nextBtnCol, getNextBtn());
    }

    private void onPrevBtnClick() {
        this.gui.previous();
        if (currentScrollNotch > 0)  currentScrollNotch --;
        if (currentScrollNotch == 0) hidePrevBtn();
        showNextBtn();
    }

    private void onNextBtnClick() {
        this.gui.next();
        if (currentScrollNotch < numScrollNotches) currentScrollNotch ++;
        if (currentScrollNotch == numScrollNotches) hideNextBtn();
        showPrevBtn();
    }

    private int getNumScrollNotches() {
        int numScrollNotches = numItems > itemWindowSize ? (numItems - itemWindowSize + scrollNotchSize - 1) / scrollNotchSize : 0;
        if (numScrollNotches > 0 && ((numItems % scrollNotchSize) == 0)) numScrollNotches ++;
        return numScrollNotches;
    }


    protected void setItems(List<GuiItem> guiItems) {
        guiItems.forEach(gui::addItem);
        numItems = guiItems.size();
        numScrollNotches = getNumScrollNotches();
        if (numScrollNotches > 0 && currentScrollNotch < numScrollNotches) showNextBtn(); //TODO Test this!!!
    }

    protected void addItem(GuiItem guiItem) {
        gui.addItem(guiItem);
        numItems++;
        numScrollNotches = getNumScrollNotches();
        if (numScrollNotches > 0 && currentScrollNotch < numScrollNotches) showNextBtn(); //TODO Test this!!!
        // Do we need an update here?
    }

    protected void update() {
        this.gui.update();
    }

    public void open(HumanEntity player) {
        gui.open(player);
    }

    public void setOnClose(GuiAction<InventoryCloseEvent> closeGuiAction) {
        gui.setCloseGuiAction(closeGuiAction);
    }
}
