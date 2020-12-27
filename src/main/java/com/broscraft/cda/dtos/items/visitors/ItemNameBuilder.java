package com.broscraft.cda.dtos.items.visitors;


import com.broscraft.cda.dtos.items.EnchantedItemDTO;
import com.broscraft.cda.dtos.items.ItemDTO;
import com.broscraft.cda.dtos.items.PotionDTO;
import com.broscraft.cda.utils.RomanNumber;

import org.apache.commons.lang.WordUtils;

public class ItemNameBuilder extends ItemVisitor {
    private StringBuilder builder;


    public String getItemName() {
        return WordUtils.capitalize(builder.toString());
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
        enchantedItemDto.getEnchantments().forEach(enchantment -> {
            String enchantmentName = enchantment.getEnchantment().replaceAll("[_\\s]", " ").toLowerCase();
            builder.append(" " + enchantmentName)
                   .append(" " + RomanNumber.toRoman(enchantment.getLevel()).toUpperCase());
        });
    }

    @Override
    public void visit(PotionDTO potionDto) {
        this.setMaterialName(potionDto);
        String potionName = potionDto.getType().name().replace("_", " ").toLowerCase();
        builder.append(" " + potionName);
        if (potionDto.getUpgraded()) {
            builder.append(" II");
        }
    }
    
}