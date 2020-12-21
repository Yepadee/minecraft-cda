package com.broscraft.cda.model.items;

import java.util.Objects;

public class EnchantmentDTO {
    private String enchantment;
    private Integer level;

    public EnchantmentDTO() {
    }

    public EnchantmentDTO(String enchantment, Integer level) {
        this.enchantment = enchantment;
        this.level = level;
    }

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

    public EnchantmentDTO enchantment(String enchantment) {
        this.enchantment = enchantment;
        return this;
    }

    public EnchantmentDTO level(Integer level) {
        this.level = level;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof EnchantmentDTO)) {
            return false;
        }
        EnchantmentDTO enchantmentDTO = (EnchantmentDTO) o;
        return Objects.equals(enchantment, enchantmentDTO.enchantment) && Objects.equals(level, enchantmentDTO.level);
    }

    @Override
    public int hashCode() {
        return Objects.hash(enchantment, level);
    }

    @Override
    public String toString() {
        return "{" +
            " enchantment='" + getEnchantment() + "'" +
            ", level='" + getLevel() + "'" +
            "}";
    }

}
