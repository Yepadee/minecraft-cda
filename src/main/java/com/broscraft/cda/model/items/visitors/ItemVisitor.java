package com.broscraft.cda.model.items.visitors;

import com.broscraft.cda.model.items.EnchantedItemDTO;
import com.broscraft.cda.model.items.ItemDTO;
import com.broscraft.cda.model.items.PotionDTO;

public abstract class ItemVisitor {
    public abstract void visit(ItemDTO itemDto);
    public abstract void visit(EnchantedItemDTO enchantedItemDto);
    public abstract void visit(PotionDTO potionDto);
}
