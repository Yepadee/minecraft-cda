package com.broscraft.cda.model.items;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import com.broscraft.cda.model.items.visitors.ItemNameBuilder;
import com.broscraft.utils.ItemUitls;

import org.junit.Test;

public class ItemNameTest {

    @Test
    public void testNameBuilder()
    {
        ItemNameBuilder nameBuilder = new ItemNameBuilder();
        List<ItemDTO> items = ItemUitls.buildAllItems();
        for (ItemDTO item : items) {
            item.accept(nameBuilder);
            System.out.println(nameBuilder.getItemName());
        }
    }
}
