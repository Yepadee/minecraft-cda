package com.broscraft.cda.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.broscraft.cda.dtos.items.EnchantedItemDTO;
import com.broscraft.cda.dtos.items.EnchantmentDTO;
import com.broscraft.cda.dtos.items.ItemDTO;
import com.broscraft.cda.dtos.items.PotionDTO;
import com.broscraft.cda.dtos.items.visitors.SearchableNameBuilder;
import com.broscraft.cda.dtos.items.visitors.ItemNameBuilder;
import com.broscraft.cda.dtos.items.visitors.ItemStackBuilder;
import com.broscraft.cda.dtos.orders.grouped.GroupedOrderDTO;
import com.broscraft.cda.dtos.orders.grouped.visitors.GroupedOrderIconBuilder;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionType;


public class ItemUtils {
    private static final ItemNameBuilder itemNameBuilder = new ItemNameBuilder();
    private static final SearchableNameBuilder searchableNameBuilder = new SearchableNameBuilder();
    private static final ItemStackBuilder itemStackBuilder = new ItemStackBuilder();
    private static final GroupedOrderIconBuilder groupedOrderIconBuilder = new GroupedOrderIconBuilder();

    private static final NamespacedKey ICON_ID_KEY = new NamespacedKey(JavaPlugin.getProvidingPlugin(ItemStackBuilder.class), "icon_id");

    private static final Set<String> NAME_CONTAINS_BLACKLIST = new HashSet<>(
        Arrays.asList("LEGACY",
                "AIR",
                "SPAWN_EGG",
                "SPAWNER",
                "POTTED",
                "WALL_BANNER",
                "WALL_HEAD",
                "WALL_SKULL",
                "WALL_FAN",
                "WALL_SIGN",
                "WALL_TORCH",
                "ATTACHED",
                "COMMAND_BLOCK"
            )
        );

    private static final Set<Material> BLACKLIST = new HashSet<>(
        Arrays.asList(
            Material.BEDROCK,
            Material.TRIPWIRE,
            Material.WATER,
            Material.LAVA,
            Material.PISTON_HEAD,
            Material.MOVING_PISTON,
            Material.FIRE,
            Material.SOUL_FIRE,
            Material.REDSTONE_WIRE,
            Material.PUMPKIN_STEM,
            Material.MELON_STEM,
            Material.TALL_SEAGRASS,
            Material.NETHER_PORTAL,
            Material.END_PORTAL,
            Material.COCOA,
            Material.CARROTS,
            Material.POTATOES,
            Material.BEETROOT,
            Material.END_GATEWAY,
            Material.FROSTED_ICE,
            Material.KELP_PLANT,
            Material.BAMBOO_SAPLING,
            Material.BUBBLE_COLUMN,
            Material.SWEET_BERRY_BUSH,
            Material.WEEPING_VINES_PLANT,
            Material.TWISTING_VINES_PLANT,
            Material.BARRIER,
            Material.STRUCTURE_VOID,
            Material.STRUCTURE_BLOCK,
            Material.JIGSAW,
            Material.POTION,
            Material.WRITTEN_BOOK,
            Material.POTION,
            Material.LINGERING_POTION,
            Material.SPLASH_POTION,
            Material.ENCHANTED_BOOK,
            Material.KNOWLEDGE_BOOK)
        );
                
    public static List<ItemDTO> buildAllItems() {
        Long id = 1L;
        List<ItemDTO> items = new ArrayList<>();
        for (Material material : Material.values()) {
            if (NAME_CONTAINS_BLACKLIST.stream().noneMatch(s -> material.name().contains(s)) && !BLACKLIST.contains(material)) {
                ItemDTO itemDTO = new ItemDTO();
                itemDTO.setMaterial(material);
                itemDTO.setId(id);
                items.add(itemDTO);
                id ++;
            }
        }

        return items;
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

    public static Long getId(ItemStack icon) {
        if (icon != null && icon.getItemMeta() != null) {
            return icon.getItemMeta().getPersistentDataContainer()
                    .get(ICON_ID_KEY, PersistentDataType.LONG);
        }
        return null;
    }

    public static void setId(ItemStack icon, long id) {
        if (icon.getItemMeta() != null) {
            ItemMeta meta = icon.getItemMeta();
            meta.getPersistentDataContainer().set(ICON_ID_KEY, PersistentDataType.LONG, id);
            icon.setItemMeta(meta);
        }
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
