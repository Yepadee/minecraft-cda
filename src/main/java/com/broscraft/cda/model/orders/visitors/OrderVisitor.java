package com.broscraft.cda.model.orders.visitors;

import com.broscraft.cda.model.orders.AskDTO;
import com.broscraft.cda.model.orders.BidDTO;

public abstract class OrderVisitor {
    public abstract void visit(BidDTO bidDTO);
    public abstract void visit(AskDTO askDTO);
}
