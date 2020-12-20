package com.broscraft.cda.model.items.visitors;

import java.util.Arrays;

import com.broscraft.cda.model.items.EnchantedItemDTO;
import com.broscraft.cda.model.items.ItemDTO;
import com.broscraft.cda.model.items.PotionDTO;

public class ItemNameBuilder extends ItemVisitor {
    private StringBuilder builder;

    private static String repeat(char c, int amount) {
        char[] chars = new char[amount];
        Arrays.fill(chars, c);
        return new String(chars);
    }

    public ItemNameBuilder() {
    }

    public String getItemName() {
        return builder.toString();
    }

    public void setMaterialName(ItemDTO itemEntity) {
        this.builder = new StringBuilder();
        this.builder.append(itemEntity.getMaterial().name().replace("_", " ").toLowerCase());
    }

    @Override
    public void visit(ItemDTO itemDto) {
        setMaterialName(itemDto);
    }

    @Override
    public void visit(EnchantedItemDTO enchantedItemDto) {
        this.setMaterialName(enchantedItemDto);
        enchantedItemDto.getEnchantments().forEach((enchantment) -> {
            String enchantmentName = enchantment.getEnchantment().replaceAll("[_\\s]", " ").toLowerCase();
            builder.append(enchantmentName).append(enchantment.getLevel() + 1).append(enchantmentName)
                    .append(repeat('i', enchantment.getLevel() + 1));
        });
    }

    @Override
    public void visit(PotionDTO potionDto) {
        //this.setMaterialName(potionDto);
        String potionName = potionDto.getType().name().replace("_", " ").toLowerCase();
        builder.append(potionName);
        if (potionDto.getUpgraded()) {
            builder.append(2).append(potionName).append("ii");
        }
    }
    
}