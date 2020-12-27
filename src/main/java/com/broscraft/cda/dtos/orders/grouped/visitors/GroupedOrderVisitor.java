package com.broscraft.cda.dtos.orders.grouped.visitors;

import com.broscraft.cda.dtos.orders.grouped.GroupedAskDTO;
import com.broscraft.cda.dtos.orders.grouped.GroupedBidDTO;

public abstract class GroupedOrderVisitor {
    public abstract void visit(GroupedBidDTO groupedBid);
    public abstract void visit(GroupedAskDTO groupedAsk);
}
