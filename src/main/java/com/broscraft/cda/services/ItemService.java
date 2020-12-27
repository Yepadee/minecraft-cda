package com.broscraft.cda.services;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.broscraft.cda.dtos.ItemOverviewDTO;
import com.broscraft.cda.dtos.items.ItemDTO;
import com.broscraft.cda.repositories.ItemRepository;

public class ItemService {
    private ItemRepository itemRepository;

    private Map<ItemDTO, Long> itemIds;
    private Map<Long, ItemOverviewDTO> itemOverviews;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
        itemOverviews = itemRepository.getItemOverviews();
        itemIds = new HashMap<>();
        itemOverviews.forEach((itemId, itemOverview) -> {
            itemIds.put(itemOverview.getItem(), itemId);
        });
    }

    public Collection<ItemOverviewDTO> getItemOverviews() {
        return this.itemOverviews.values();
    }

    public ItemOverviewDTO getItemOverview(Long itemId) {
        return this.itemOverviews.get(itemId);
    }
    
    public Long getItemId(ItemDTO itemDTO) {
        Long itemId = itemIds.get(itemDTO);
        return itemId;
    }

    public boolean exists(ItemDTO itemDTO) {
        return itemIds.containsKey(itemDTO);
    }

    public Long createItem(ItemDTO itemDTO) {
        Long itemId = itemRepository.create(itemDTO);
        itemIds.put(itemDTO, itemId);

        ItemOverviewDTO itemOverview = new ItemOverviewDTO();
        itemOverview.setItem(itemDTO);
        itemOverviews.put(itemId, itemOverview);
        return itemId;
    }
}
