package com.broscraft.cda.dtos.items;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.broscraft.cda.dtos.items.visitors.ItemVisitor;

public class EnchantedItemDTO extends ItemDTO {
    private List<EnchantmentDTO> enchantments = new ArrayList<>();

    public EnchantedItemDTO() {
    }

    public EnchantedItemDTO(List<EnchantmentDTO> enchantments) {
        this.enchantments = enchantments;
    }

    public List<EnchantmentDTO> getEnchantments() {
        return this.enchantments;
    }

    public void setEnchantments(List<EnchantmentDTO> enchantments) {
        this.enchantments = enchantments;
    }

    public EnchantedItemDTO enchantments(List<EnchantmentDTO> enchantments) {
        this.enchantments = enchantments;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof EnchantedItemDTO)) {
            return false;
        }
        EnchantedItemDTO enchantedItemDTO = (EnchantedItemDTO) o;
        return Objects.equals(enchantments, enchantedItemDTO.enchantments) &&
        Objects.equals(this.getMaterial(), enchantedItemDTO.getMaterial());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getMaterial(), enchantments);
    }

    @Override
    public String toString() {
        return "{" +
            " id='" + getId() + "'" +
            ", material='" + getMaterial() + "'" +
            ", enchantments='" + getEnchantments() + "'" +
            "}";
    }

    public void accept(ItemVisitor v) {
        v.visit(this);
    }
}
