package com.broscraft.cda.model.items;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.junit.Test;

public class ItemEqualityTest {

    @Test
    public void testItemsEqual()
    {
        EnchantedItemDTO item1 = new EnchantedItemDTO();
        item1.setMaterial(Material.DIAMOND_SWORD);

        List<EnchantmentDTO> enchantments1 = new ArrayList<>();
        EnchantmentDTO enchantment1 = new EnchantmentDTO();
        enchantment1.setEnchantment("DAMAGE_ALL");
        enchantment1.setLevel(3);
        enchantments1.add(enchantment1);

        item1.setEnchantments(enchantments1);

        EnchantedItemDTO item2 = new EnchantedItemDTO();
        item2.setMaterial(Material.DIAMOND_SWORD);

        List<EnchantmentDTO> enchantments2 = new ArrayList<>();
        EnchantmentDTO enchantment2 = new EnchantmentDTO();
        enchantment2.setEnchantment("DAMAGE_ALL");
        enchantment2.setLevel(3);
        enchantments2.add(enchantment2);

        item2.setEnchantments(enchantments2);
        item1.setId(1L);
        
        Set<ItemDTO> items = new HashSet<>();
        items.add(item1);
        System.out.println(items.contains(item2));
    }
}
