package com.broscraft.cda.utils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.broscraft.cda.dtos.items.EnchantedItemDTO;
import com.broscraft.cda.dtos.items.EnchantmentDTO;
import com.broscraft.cda.dtos.items.ItemDTO;
import com.broscraft.cda.dtos.items.PotionDTO;
import com.broscraft.cda.dtos.items.visitors.ItemNameBuilder;
import com.broscraft.cda.dtos.items.visitors.ItemStackBuilder;
import com.broscraft.cda.dtos.items.visitors.SearchableNameBuilder;
import com.broscraft.cda.dtos.orders.grouped.GroupedOrderDTO;
import com.broscraft.cda.dtos.orders.grouped.visitors.GroupedOrderIconBuilder;
import com.broscraft.cda.services.ItemService;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;


public class ItemUtils {
    private static final ItemNameBuilder itemNameBuilder = new ItemNameBuilder();
    private static final SearchableNameBuilder searchableNameBuilder = new SearchableNameBuilder();
    private static final ItemStackBuilder itemStackBuilder = new ItemStackBuilder();
    private static final GroupedOrderIconBuilder groupedOrderIconBuilder = new GroupedOrderIconBuilder();
    private static ItemService itemService;

    public static void init(ItemService itemService) {
        ItemUtils.itemService = itemService;
    }
                
    public static ItemStack hideAttributes(ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static String getItemName(ItemDTO itemDTO) {
        itemDTO.accept(itemNameBuilder);
        return itemNameBuilder.getItemName();
    }

    public static String getSearchableName(ItemDTO itemDTO) {
        itemDTO.accept(searchableNameBuilder);
        return searchableNameBuilder.getSearchableName();
    }

    public static ItemStack buildItemStack(ItemDTO itemDTO) {
        itemDTO.accept(itemStackBuilder);
        return itemStackBuilder.getIcon();
    }

    public static ItemStack createGroupedOrderIcon(GroupedOrderDTO groupedOrderDTO) {
        groupedOrderDTO.accept(groupedOrderIconBuilder);
        return groupedOrderIconBuilder.getIcon();
    }

    public static ItemDTO parseItemStack(ItemStack itemStack) {
        ItemDTO itemDTO;
        Map<Enchantment, Integer> enchantments;
        if (itemStack.getType().equals(Material.ENCHANTED_BOOK)) {
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) itemStack.getItemMeta();
            enchantments = meta.getStoredEnchants();
        } else {
            enchantments = itemStack.getEnchantments();
        }

        if (enchantments.size() > 0) {
            EnchantedItemDTO enchantedItemDTO = new EnchantedItemDTO();
            // Parse enchantments from itemstack
            List<EnchantmentDTO> enchantmentDTOs = enchantments.entrySet().stream().map(entry -> {
                EnchantmentDTO enchantmentDTO = new EnchantmentDTO();
                enchantmentDTO.setEnchantment(entry.getKey().getKey().getKey());
                enchantmentDTO.setLevel(entry.getValue());
                return enchantmentDTO;
            }).collect(Collectors.toList());
            enchantedItemDTO.setEnchantments(enchantmentDTOs);
            itemDTO = enchantedItemDTO;
        } else if(itemStack.getType().equals(Material.POTION) || itemStack.getType().equals(Material.SPLASH_POTION)) {
            PotionDTO potionDTO = new PotionDTO();
            PotionMeta pm = (PotionMeta) itemStack.getItemMeta();
            
            Boolean extended = pm.getBasePotionData().isExtended();
            Boolean upgraded = pm.getBasePotionData().isUpgraded();
            PotionType type = pm.getBasePotionData().getType();

            potionDTO.setExtended(extended);
            potionDTO.setUpgraded(upgraded);
            potionDTO.setType(type);

            itemDTO = potionDTO;
        } else {
            itemDTO = new ItemDTO();
        }

        itemDTO.setMaterial(itemStack.getType());
        itemDTO.setId(itemService.getItemId(itemDTO));
        return itemDTO;
    }

    public static boolean isDamaged(ItemStack item) {
        if (item.getItemMeta() instanceof Damageable) {
            Damageable meta = (Damageable) item.getItemMeta();
            return meta.hasDamage();
        } else {
            return false;
        }
    }

}
