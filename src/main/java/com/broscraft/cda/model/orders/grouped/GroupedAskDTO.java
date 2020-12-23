package com.broscraft.cda.model.orders.grouped;

import com.broscraft.cda.model.orders.grouped.visitors.GroupedOrderVisitor;

public class GroupedAskDTO extends GroupedOrderDTO {

    @Override
    public void accept(GroupedOrderVisitor v) {
        v.visit(this);
    }
    
}