package com.broscraft.cda.model.orders.grouped.visitors;

import com.broscraft.cda.model.orders.grouped.GroupedAskDTO;
import com.broscraft.cda.model.orders.grouped.GroupedBidDTO;

public abstract class GroupedOrderVisitor {
    public abstract void visit(GroupedBidDTO groupedBid);
    public abstract void visit(GroupedAskDTO groupedAsk);
}
