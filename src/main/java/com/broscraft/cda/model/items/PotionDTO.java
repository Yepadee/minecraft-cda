package com.broscraft.cda.model.items;

import java.util.Objects;

import com.broscraft.cda.model.items.visitors.ItemVisitor;

import org.bukkit.potion.PotionType;

public class PotionDTO extends ItemDTO {
    private PotionType type;
    private Boolean upgraded;
    private Boolean extended;


    public PotionDTO() {
    }

    public PotionDTO(PotionType type, Boolean upgraded, Boolean extended) {
        this.type = type;
        this.upgraded = upgraded;
        this.extended = extended;
    }

    public PotionType getType() {
        return this.type;
    }

    public void setType(PotionType type) {
        this.type = type;
    }

    public Boolean isUpgraded() {
        return this.upgraded;
    }

    public Boolean getUpgraded() {
        return this.upgraded;
    }

    public void setUpgraded(Boolean upgraded) {
        this.upgraded = upgraded;
    }

    public Boolean isExtended() {
        return this.extended;
    }

    public Boolean getExtended() {
        return this.extended;
    }

    public void setExtended(Boolean extended) {
        this.extended = extended;
    }

    public PotionDTO type(PotionType type) {
        this.type = type;
        return this;
    }

    public PotionDTO upgraded(Boolean upgraded) {
        this.upgraded = upgraded;
        return this;
    }

    public PotionDTO extended(Boolean extended) {
        this.extended = extended;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof PotionDTO)) {
            return false;
        }
        PotionDTO potionDTO = (PotionDTO) o;
        return Objects.equals(type, potionDTO.type) && Objects.equals(upgraded, potionDTO.upgraded) && Objects.equals(extended, potionDTO.extended) &&
        Objects.equals(this.getMaterial(), potionDTO.getMaterial());
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, upgraded, extended);
    }

    @Override
    public String toString() {
        return "{" +
            " type='" + getType() + "'" +
            ", upgraded='" + isUpgraded() + "'" +
            ", extended='" + isExtended() + "'" +
            "}";
    }

    public void accept(ItemVisitor v) {
        v.visit(this);
    }
}
