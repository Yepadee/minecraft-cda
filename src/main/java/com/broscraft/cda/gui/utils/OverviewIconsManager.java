package com.broscraft.cda.gui.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.broscraft.cda.model.ItemOverviewDTO;
import com.broscraft.cda.observers.IconUpdateObserver;
import com.broscraft.cda.observers.NewIconObserver;
import com.broscraft.cda.observers.OverviewUpdateObserver;
import com.broscraft.utils.ItemUitls;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.md_5.bungee.api.ChatColor;

public class OverviewIconsManager implements OverviewUpdateObserver {
    private Map<Long, ItemStack> icons = new HashMap<>();
    private Map<Long, String> iconNames = new HashMap<>();

    private List<IconUpdateObserver> iconUpdateObservers = new ArrayList<>();
    private List<NewIconObserver> newIconObservers = new ArrayList<>();


    public void addIconUpdateObserver(IconUpdateObserver o) {
        iconUpdateObservers.add(o);
    }

    public void removeIconUpdateObserver(IconUpdateObserver o) {
        iconUpdateObservers.remove(o);
    }

    public void addNewIconObserver(NewIconObserver o) {
        newIconObservers.add(o);
    }

    public void removeNewIconObserver(NewIconObserver o) {
        newIconObservers.remove(o);
    }


    private void notifyIconUpdateObservers() {
        iconUpdateObservers.forEach(o ->  o.onIconUpdate());
    }

    private void notifyNewIconObservers(ItemStack icon) {
        newIconObservers.forEach(o ->  o.onNewIcon(icon));
    }


    public Collection<ItemStack> getAllIcons() {
        return this.icons.values();
    }

    public Collection<ItemStack> searchIcons(String searchQuery) {
        Collection<ItemStack> results = new ArrayList<>();
        results = icons.keySet().stream().filter(k -> {
            String iconName = this.iconNames.get(k);
            return iconName.contains(searchQuery);
        }).map(k -> this.icons.get(k))
        .collect(Collectors.toList());
        return results;
    }

    public void createIcons(List<ItemOverviewDTO> itemOverviews) {
        itemOverviews.forEach(itemOverview -> {
            createIcon(itemOverview);
        });
    }

    private ItemStack createIcon(ItemOverviewDTO itemOverview) {
        ItemStack icon = ItemUitls.createIcon(itemOverview.getItem());
        List<String> lore = this.getLore(itemOverview);
        ItemMeta meta = icon.getItemMeta();

        Long id = itemOverview.getItem().getId();

        meta.setLore(lore);
        icon.setItemMeta(meta);
        iconNames.put(id, ItemUitls.getItemName(itemOverview.getItem()));
        icons.put(id, icon);
        return icon;
    }

    private void updateIcon(ItemOverviewDTO itemOverviewDTO) {
        Long itemId = Objects.requireNonNull(itemOverviewDTO.getItem().getId());
        ItemStack icon = this.icons.get(itemId);
        List<String> lore = this.getLore(itemOverviewDTO);
        ItemMeta meta = icon.getItemMeta();
        meta.setLore(lore);
        icon.setItemMeta(meta);
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

        if (this.icons.containsKey(itemId)) {
            this.updateIcon(itemOverviewDTO);
            this.notifyIconUpdateObservers();
        } else {
            ItemStack icon = createIcon(itemOverviewDTO);
            this.notifyNewIconObservers(icon);
        }
    }

    @Override
    public void onOverviewLoad(Collection<ItemOverviewDTO> itemOverviewDTOs) {
        itemOverviewDTOs.forEach(itemOverviewDTO -> {
            createIcon(itemOverviewDTO);
        });
    }
}
