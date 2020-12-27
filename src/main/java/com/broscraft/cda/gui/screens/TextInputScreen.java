package com.broscraft.cda.gui.screens;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.broscraft.cda.CDAPlugin;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import net.wesjd.anvilgui.AnvilGUI;

public abstract class TextInputScreen {
    private AnvilGUI.Builder gui = new AnvilGUI.Builder();

    public TextInputScreen(
        String name,
        String placeholder,
        ItemStack confirmIcon,
        BiConsumer<Player, String> onConfirmBtnClick,
        Consumer<Player> onClose
    ) {
        setUpScreen(
            name,
            placeholder,
            confirmIcon,
            onConfirmBtnClick,
            onClose
        );
    }

    private void setUpScreen(
        String name,
        String placeholder,
        ItemStack confirmIcon,
        BiConsumer<Player, String> onConfirmBtnClick,
        Consumer<Player> onClose
    ) {
        this.gui.plugin(JavaPlugin.getProvidingPlugin(this.getClass()))
        .title(name)
        .text(placeholder)
        .itemLeft(confirmIcon)
        .onComplete((p, text) -> {
            onConfirmBtnClick.accept(p, text);
            return AnvilGUI.Response.close();
        })
        .onClose(p -> CDAPlugin.runTask(() -> onClose.accept(p)));
    }

    protected void setOnClose(Consumer<Player> onClose) {
        this.gui.onClose(onClose);
    }

    public void open(HumanEntity player) {
        this.gui.open((Player) player);
    }
}
