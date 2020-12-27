package com.broscraft.cda.dtos.orders.grouped;

import com.broscraft.cda.dtos.orders.grouped.visitors.GroupedOrderVisitor;

public class GroupedAskDTO extends GroupedOrderDTO {

    @Override
    public void accept(GroupedOrderVisitor v) {
        v.visit(this);
    }
    
}
