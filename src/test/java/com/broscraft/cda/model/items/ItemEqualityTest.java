package com.broscraft.cda.model.items;

import java.util.ArrayList;
import java.util.List;

import com.broscraft.utils.ItemUitls;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
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
        String e = Enchantment.DAMAGE_ALL.toString();
        System.out.println(e);
    }
}
