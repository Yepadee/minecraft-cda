package com.broscraft.cda.model.items;

public class EnchantmentDTO {
    private String enchantment;
    private Integer level;

    public EnchantmentDTO() {}

    public String getEnchantment() {
        return this.enchantment;
    }

    public void setEnchantment(String enchantment) {
        this.enchantment = enchantment;
    }

    public Integer getLevel() {
        return this.level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

}
