package com.broscraft.cda.model.items;

import java.util.List;

import com.broscraft.cda.utils.ItemUitls;

import org.junit.Test;

public class ItemNameTest {

    @Test
    public void testNameBuilder()
    {
        List<ItemDTO> items = ItemUitls.buildAllItems();
        for (ItemDTO item : items) {
            System.out.println(ItemUitls.getItemName(item));
        }
    }
}
