package com.broscraft.cda.model.items;

import org.bukkit.Material;

public class ItemDTO {
    private Long id;
    private Material material;

    public ItemDTO() {}

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

    public void setMaterial(String material) {
        this.material = Material.valueOf(material);
    }

}
