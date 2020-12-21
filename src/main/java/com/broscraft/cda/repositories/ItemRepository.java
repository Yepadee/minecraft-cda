package com.broscraft.cda.repositories;

import com.broscraft.cda.model.items.ItemDTO;

public class ItemRepository {
    //TEMP:
    Long lastId = 3L;

    public Long getItemId(ItemDTO itemDTO) {
        // TODO:
        // Check if item exisists in database, if not insert it and retrieve the id.
        // The item in newOrderDTO will have it's id set here.
        return lastId ++;
    }
}
