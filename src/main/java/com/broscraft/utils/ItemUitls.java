package com.broscraft.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.broscraft.cda.model.items.ItemDTO;
import com.broscraft.cda.model.items.visitors.ItemNameBuilder;

import org.bukkit.Material;

public class ItemUitls {
    private static final ItemNameBuilder itemNameBuilder = new ItemNameBuilder();

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

    public static String getItemName(ItemDTO itemDTO) {
        itemDTO.accept(itemNameBuilder);
        return itemNameBuilder.getItemName();
    }
}
