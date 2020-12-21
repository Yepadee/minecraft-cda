package com.broscraft.cda.gui;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.broscraft.cda.model.ItemOverviewDTO;
import com.broscraft.cda.observers.IconUpdateObserver;
import com.broscraft.cda.observers.OverviewUpdateObserver;
import com.broscraft.utils.ItemUitls;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.md_5.bungee.api.ChatColor;

public class OverviewIconsManager implements OverviewUpdateObserver {
    private Map<Long, ItemStack> icons = new HashMap<>();

    private IconUpdateObserver iconUpdateObserver;

    public OverviewIconsManager(IconUpdateObserver iconUpdateObserver) {
        this.iconUpdateObserver = iconUpdateObserver;
    }

    public void createIcons(List<ItemOverviewDTO> itemOverviews) {
        itemOverviews.forEach(itemOverview -> {
            Long id = itemOverview.getItem().getId();
            ItemStack icon = createIcon(itemOverview);
            icons.put(id, icon);
        });
    }

    private ItemStack createIcon(ItemOverviewDTO itemOverview) {
        ItemStack icon = ItemUitls.createIcon(itemOverview.getItem());
        List<String> lore = this.getLore(itemOverview);
        ItemMeta meta = icon.getItemMeta();
        meta.setLore(lore);
        icon.setItemMeta(meta);
        return icon;
    }

    private List<String> getLore(ItemOverviewDTO itemOverview) {
        String notAvailable = ChatColor.RED.toString() + ChatColor.BOLD + "N/A";
        Float bestBid = itemOverview.getBestBid();
        Float bestAsk = itemOverview.getBestAsk();
        int supply = itemOverview.getSupply();
        int demand = itemOverview.getDemand();
        List<String> lore = Arrays.asList(
            "",
            ChatColor.GOLD.toString() + ChatColor.UNDERLINE + "     Bids      ",
            ChatColor.GRAY + "Best Bid: " +
            (bestBid != null ? ChatColor.GOLD.toString() + ChatColor.BOLD + bestBid : notAvailable),
            ChatColor.GRAY + "Demand: " + ChatColor.GOLD + ChatColor.BOLD + demand,
            "",
            ChatColor.AQUA.toString() + ChatColor.UNDERLINE + "     Asks     ",
            ChatColor.GRAY + "Best Ask: " +
            (bestAsk != null ? ChatColor.AQUA.toString() + ChatColor.BOLD + bestAsk : notAvailable),
            ChatColor.GRAY + "Supply: " + ChatColor.AQUA + ChatColor.BOLD + supply
        );
        return lore;
    }

    @Override
    public void onOverviewUpdate(ItemOverviewDTO itemOverviewDTO) {
        // Update icon lore when data changes
        Long itemId = Objects.requireNonNull(itemOverviewDTO.getItem().getId());
        ItemStack icon;
        // Check icon exists
        boolean newIcon = false;
        if (this.icons.containsKey(itemId)) {
            icon = this.icons.get(itemId); // If so, retrieve it
        } else {
            icon = createIcon(itemOverviewDTO);
            icons.put(itemId, icon); // Otherwise, create a new one
            newIcon = true;
        }

        List<String> lore = this.getLore(itemOverviewDTO);
        ItemMeta meta = icon.getItemMeta();
        meta.setLore(lore);
        icon.setItemMeta(meta);

        if (newIcon) iconUpdateObserver.onNewIcon(icon);
        else iconUpdateObserver.onIconUpdate();

    }

    @Override
    public void onOverviewLoad(Collection<ItemOverviewDTO> itemOverviewDTOs) {
        itemOverviewDTOs.forEach(itemOverviewDTO -> {
            Long itemId = Objects.requireNonNull(itemOverviewDTO.getItem().getId());
            ItemStack icon = createIcon(itemOverviewDTO);
            List<String> lore = this.getLore(itemOverviewDTO);
            ItemMeta meta = icon.getItemMeta();
            meta.setLore(lore);
            icon.setItemMeta(meta);
            icons.put(itemId, icon);
        });
        iconUpdateObserver.onNewIcons(this.icons.values());
    }
}
