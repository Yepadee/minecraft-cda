package com.broscraft.cda.model.items;

import java.util.List;

import com.broscraft.cda.utils.ItemUtils;

import org.junit.Test;

public class ItemNameTest {

    @Test
    public void testNameBuilder()
    {
        List<ItemDTO> items = ItemUtils.buildAllItems();
        for (ItemDTO item : items) {
            System.out.println(ItemUtils.getItemName(item));
        }
    }
}
