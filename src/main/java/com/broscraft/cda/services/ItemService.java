package com.broscraft.cda.services;

import com.broscraft.cda.model.items.ItemDTO;
import com.broscraft.cda.repositories.ItemRepository;

public class ItemService {
    private ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public Long getItemId(ItemDTO itemDTO) {
        Long itemId = itemRepository.getItemId(itemDTO);
        if (itemId == null) itemId = itemRepository.create(itemDTO);
        return itemId;
    }
}
