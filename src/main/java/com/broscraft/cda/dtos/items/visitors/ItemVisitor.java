package com.broscraft.cda.dtos.items.visitors;

import com.broscraft.cda.dtos.items.EnchantedItemDTO;
import com.broscraft.cda.dtos.items.ItemDTO;
import com.broscraft.cda.dtos.items.PotionDTO;

public abstract class ItemVisitor {
    public abstract void visit(ItemDTO itemDto);
    public abstract void visit(EnchantedItemDTO enchantedItemDto);
    public abstract void visit(PotionDTO potionDto);
}
