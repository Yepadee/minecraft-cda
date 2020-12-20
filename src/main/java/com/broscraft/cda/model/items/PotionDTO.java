package com.broscraft.cda.model.items;

import org.bukkit.potion.PotionType;

public class PotionDTO extends ItemDTO {
    private PotionType type;
    private Boolean upgraded;
    private Boolean extended;

    public PotionDTO() {}

    public PotionType getType() {
        return this.type;
    }

    public void setType(PotionType type) {
        this.type = type;
    }

    public Boolean getUpgraded() {
        return this.upgraded;
    }

    public void setUpgraded(Boolean upgraded) {
        this.upgraded = upgraded;
    }

    public Boolean getExtended() {
        return this.extended;
    }

    public void setExtended(Boolean extended) {
        this.extended = extended;
    }

}
