package com.broscraft.cda.gui.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.broscraft.cda.CDAPlugin;
import com.broscraft.cda.dtos.ItemOverviewDTO;
import com.broscraft.cda.observers.IconUpdateObserver;
import com.broscraft.cda.observers.NewIconObserver;
import com.broscraft.cda.observers.OverviewUpdateObserver;
import com.broscraft.cda.utils.ItemUtils;
import com.broscraft.cda.utils.PriceUtils;

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


    private void notifyIconUpdateObservers(Long itemId, ItemStack icon) {
        iconUpdateObservers.forEach(o ->  o.onIconUpdate(itemId, icon));
    }

    private void notifyNewIconObservers(ItemStack icon) {
        newIconObservers.forEach(o ->  o.onNewIcon(icon));
    }


    public Collection<ItemStack> getAllIcons() {
        return this.icons.values();
    }

    public void searchIcons(String searchQuery, Consumer<List<ItemStack>> onComplete) {
        CDAPlugin.newChain()
        .asyncFirst(
            () -> icons.keySet().stream().filter(k -> {
                String iconName = this.iconNames.get(k);
                return iconName.contains(searchQuery.toLowerCase());
            }).map(k -> this.icons.get(k))
            .collect(Collectors.toList())
        )
        .syncLast(result -> onComplete.accept(result))
        .execute();

    }

    public void createIcons(List<ItemOverviewDTO> itemOverviews) {
        itemOverviews.forEach(itemOverview -> {
            createIcon(itemOverview);
        });
    }

    // TODO: use itemBuilder to make
    private ItemStack createIcon(ItemOverviewDTO itemOverview) {
        ItemStack icon = ItemUtils.buildItemStack(itemOverview.getItem());
        List<String> lore = this.getLore(itemOverview);
        ItemMeta meta = icon.getItemMeta();

        Long id = itemOverview.getItem().getId();

        meta.setLore(lore);
        icon.setItemMeta(meta);
        iconNames.put(id, ItemUtils.getSearchableName(itemOverview.getItem()));
        icons.put(id, icon);
        return icon;
    }

    private ItemStack updateIcon(ItemOverviewDTO itemOverviewDTO) {
        Long itemId = Objects.requireNonNull(itemOverviewDTO.getItem().getId());
        ItemStack icon = this.icons.get(itemId);
        List<String> lore = this.getLore(itemOverviewDTO);
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
            (bestBid != null ? ChatColor.GOLD.toString() + ChatColor.BOLD + PriceUtils.formatPriceCurrency(bestBid) : notAvailable),
            ChatColor.GRAY + "Demand: " + ChatColor.GOLD + ChatColor.BOLD + demand,
            "",
            ChatColor.AQUA.toString() + ChatColor.UNDERLINE + "     Asks     ",
            ChatColor.GRAY + "Best Ask: " +
            (bestAsk != null ? ChatColor.AQUA.toString() + ChatColor.BOLD + PriceUtils.formatPriceCurrency(bestAsk) : notAvailable),
            ChatColor.GRAY + "Supply: " + ChatColor.AQUA + ChatColor.BOLD + supply
        );
        return lore;
    }

    @Override
    public void onOverviewUpdate(ItemOverviewDTO itemOverviewDTO) {
        // Update icon lore when data changes
        Long itemId = Objects.requireNonNull(itemOverviewDTO.getItem().getId());

        if (icons.containsKey(itemId)) {
            notifyIconUpdateObservers(itemId, updateIcon(itemOverviewDTO));
        } else {
            ItemStack icon = createIcon(itemOverviewDTO);
            notifyNewIconObservers(icon);
        }
    }

    @Override
    public void onOverviewLoad(Collection<ItemOverviewDTO> itemOverviewDTOs) {
        itemOverviewDTOs.forEach(itemOverviewDTO -> {
            createIcon(itemOverviewDTO);
        });
    }
}
