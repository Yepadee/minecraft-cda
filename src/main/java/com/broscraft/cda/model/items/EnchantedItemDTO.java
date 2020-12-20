package com.broscraft.cda.model.items;

import java.util.ArrayList;
import java.util.List;

public class EnchantedItemDTO extends ItemDTO {
    private List<EnchantmentDTO> enchantments = new ArrayList<>();

    public EnchantedItemDTO() {}

    public List<EnchantmentDTO> getEnchantments() {
        return this.enchantments;
    }

    public void setEnchantments(List<EnchantmentDTO> enchantments) {
        this.enchantments = enchantments;
    }
}
