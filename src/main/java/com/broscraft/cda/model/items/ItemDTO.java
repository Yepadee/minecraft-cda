package com.broscraft.cda.model.items;

import java.util.Objects;

import com.broscraft.cda.model.items.visitors.ItemVisitor;

import org.bukkit.Material;

public class ItemDTO {
    private Long id;
    private Material material;

    public ItemDTO() {
    }

    public ItemDTO(Long id, Material material) {
        this.id = id;
        this.material = material;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Material getMaterial() {
        return this.material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public ItemDTO id(Long id) {
        this.id = id;
        return this;
    }

    public ItemDTO material(Material material) {
        this.material = material;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof ItemDTO)) {
            return false;
        }
        ItemDTO itemDTO = (ItemDTO) o;
        return Objects.equals(material, itemDTO.material);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, material);
    }

    @Override
    public String toString() {
        return "{" +
            " id='" + getId() + "'" +
            ", material='" + getMaterial() + "'" +
            "}";
    }

    public void accept(ItemVisitor v) {
        v.visit(this);
    }

}
