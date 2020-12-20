package com.broscraft.cda.model.items.visitors;

import com.broscraft.cda.model.items.EnchantedItemDTO;
import com.broscraft.cda.model.items.ItemDTO;
import com.broscraft.cda.model.items.PotionDTO;
import com.broscraft.utils.ItemUitls;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;

public class IconBuilder extends ItemVisitor {
    private ItemStack icon;

    private void initialiseItem(ItemDTO itemDto) {
        this.icon = new ItemStack(itemDto.getMaterial());
        ItemUitls.setId(this.icon, itemDto.getId());
    }

    @Override
    public void visit(ItemDTO itemDto) {
        initialiseItem(itemDto);
    }

    @Override
    public void visit(EnchantedItemDTO enchantedItemDto) {
        initialiseItem(enchantedItemDto);
        enchantedItemDto.getEnchantments().forEach(enchantmentDto -> {
            Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(enchantmentDto.getEnchantment()));
            this.icon.addEnchantment(enchantment, enchantmentDto.getLevel());
        });
    }

    @Override
    public void visit(PotionDTO potionDto) {
        initialiseItem(potionDto);
        PotionMeta meta = (PotionMeta) this.icon.getItemMeta();
        meta.setBasePotionData(
            new PotionData(potionDto.getType(), potionDto.getExtended(), potionDto.getUpgraded())
        );
        this.icon.setItemMeta(meta);
    }

    public ItemStack getIcon() {
        return this.icon;
    }
    
}
