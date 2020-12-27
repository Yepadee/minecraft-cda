package com.broscraft.cda.dtos.items.visitors;


import com.broscraft.cda.dtos.items.EnchantedItemDTO;
import com.broscraft.cda.dtos.items.ItemDTO;
import com.broscraft.cda.dtos.items.PotionDTO;
import com.broscraft.cda.utils.RomanNumber;

public class SearchableNameBuilder extends ItemVisitor {
    private StringBuilder builder;

    public String getSearchableName() {
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
        enchantedItemDto.getEnchantments().forEach(enchantment -> {
            String enchantmentName = enchantment.getEnchantment().replaceAll("[_\\s]", " ").toLowerCase();
            builder.append(" " + enchantmentName)
                   .append(" " + enchantment.getLevel())
                   .append(" " + enchantmentName)
                   .append(" " + RomanNumber.toRoman(enchantment.getLevel()));
        });
    }

    @Override
    public void visit(PotionDTO potionDto) {
        this.setMaterialName(potionDto);
        String potionName = potionDto.getType().name().replace("_", " ").toLowerCase();
        builder.append(" " + potionName);
        if (potionDto.getUpgraded()) {
            builder.append(" 2 ")
            .append(potionName).append(" ii");
        }
    }
    
}