package com.broscraft.cda.gui.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.broscraft.cda.CDAPlugin;
import com.broscraft.cda.dtos.ItemOverviewDTO;
import com.broscraft.cda.dtos.items.ItemDTO;
import com.broscraft.cda.observers.IconUpdateObserver;
import com.broscraft.cda.observers.NewIconObserver;
import com.broscraft.cda.observers.OverviewUpdateObserver;
import com.broscraft.cda.utils.ItemUtils;
import com.broscraft.cda.utils.EcoUtils;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.md_5.bungee.api.ChatColor;

public class IconsManager implements OverviewUpdateObserver {
    private Map<ItemDTO, ItemStack> overviewIcons = new HashMap<>();
    private Map<ItemDTO, ItemStack> icons = new HashMap<>();
    private Map<ItemDTO, String> iconSearchNames = new HashMap<>();

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


    private void notifyIconUpdateObservers(ItemDTO itemDTO, ItemStack icon) {
        iconUpdateObservers.forEach(o ->  o.onIconUpdate(itemDTO, icon));
    }

    private void notifyNewIconObservers(ItemDTO item, ItemStack icon) {
        newIconObservers.forEach(o ->  o.onNewIcon(item, icon));
    }


    public Map<ItemDTO, ItemStack> getAllOverviewIcons() {
        return this.overviewIcons;
    }

    public ItemStack getItemIcon(ItemDTO itemDTO) {
        return this.icons.get(itemDTO);
    }

    public ItemStack getOverviewIcon(ItemDTO itemDTO) {
        return this.overviewIcons.get(itemDTO);
    }

    public boolean hasIcon(ItemDTO itemDTO) {
        return this.overviewIcons.containsKey(itemDTO);
    }

    public void searchIcons(String searchQuery, Consumer<Map<ItemDTO, ItemStack>> onComplete) {
        CDAPlugin.newChain()
        .asyncFirst(
            () -> icons.entrySet().stream().filter(entry -> {
                ItemDTO itemDTO = entry.getKey();
                String iconName = this.iconSearchNames.get(itemDTO);
                return iconName.contains(searchQuery.toLowerCase());
            })
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
        )
        .syncLast(result -> onComplete.accept(result))
        .execute();

    }

    public void createIcons(List<ItemOverviewDTO> itemOverviews) {
        itemOverviews.forEach(itemOverview -> {
            createOverviewIcon(itemOverview);
        });
    }

    // TODO: use itemBuilder to make
    private ItemStack createOverviewIcon(ItemOverviewDTO itemOverview) {
        ItemDTO itemDTO = itemOverview.getItem();
        ItemStack icon = ItemUtils.buildItemStack(itemDTO);
        icons.put(itemDTO, icon);

        ItemStack overviewIcon = icon.clone();
        List<String> lore = this.getLore(itemOverview);
        ItemMeta meta = overviewIcon.getItemMeta();
        meta.setLore(lore);
        overviewIcon.setItemMeta(meta);

        iconSearchNames.put(itemDTO, ItemUtils.getSearchableName(itemOverview.getItem()));
        overviewIcons.put(itemDTO, overviewIcon);
        return icon;
    }

    private ItemStack updateIcon(ItemOverviewDTO itemOverviewDTO) {
        ItemDTO itemDTO = itemOverviewDTO.getItem();
        ItemStack icon = this.overviewIcons.get(itemDTO);
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
            (bestBid != null && demand > 0 ? ChatColor.GOLD.toString() + ChatColor.BOLD + EcoUtils.formatPriceCurrency(bestBid) : notAvailable),
            ChatColor.GRAY + "Demand: " + ChatColor.GOLD + ChatColor.BOLD + demand,
            "",
            ChatColor.AQUA.toString() + ChatColor.UNDERLINE + "     Asks     ",
            ChatColor.GRAY + "Best Ask: " +
            (bestAsk != null  && supply > 0 ? ChatColor.AQUA.toString() + ChatColor.BOLD + EcoUtils.formatPriceCurrency(bestAsk) : notAvailable),
            ChatColor.GRAY + "Supply: " + ChatColor.AQUA + ChatColor.BOLD + supply
        );
        return lore;
    }

    @Override
    public void onOverviewUpdate(ItemOverviewDTO itemOverviewDTO) {
        // Update icon lore when data changes
        ItemDTO itemDTO = itemOverviewDTO.getItem();
        ItemStack newIcon; 
        if (icons.containsKey(itemDTO)) {
            newIcon = updateIcon(itemOverviewDTO);
            notifyIconUpdateObservers(itemDTO, newIcon);
        } else {
            newIcon = createOverviewIcon(itemOverviewDTO);
            notifyNewIconObservers(itemDTO, newIcon);
        }
        System.out.println("UPDATED ICONS: ");
        System.out.println(this.overviewIcons);
        
    }

    @Override
    public void onOverviewLoad(Collection<ItemOverviewDTO> itemOverviewDTOs) {
        itemOverviewDTOs.forEach(itemOverviewDTO -> {
            createOverviewIcon(itemOverviewDTO);
        });
    }
}
