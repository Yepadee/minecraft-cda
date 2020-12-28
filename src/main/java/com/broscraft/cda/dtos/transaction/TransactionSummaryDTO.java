package com.broscraft.cda.dtos.transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.broscraft.cda.dtos.orders.OrderDTO;

public class TransactionSummaryDTO {
    List<OrderDTO> affectedOrders = new ArrayList<>();
    Integer numFilled;

    public TransactionSummaryDTO() {
    }

    public TransactionSummaryDTO(List<OrderDTO> affectedOrders, Integer numFilled) {
        this.affectedOrders = affectedOrders;
        this.numFilled = numFilled;
    }

    public List<OrderDTO> getAffectedOrders() {
        return this.affectedOrders;
    }

    public void setAffectedOrders(List<OrderDTO> affectedOrders) {
        this.affectedOrders = affectedOrders;
    }

    public Integer getNumFilled() {
        return this.numFilled;
    }

    public void setNumFilled(Integer numFilled) {
        this.numFilled = numFilled;
    }

    public TransactionSummaryDTO affectedOrders(List<OrderDTO> affectedOrders) {
        this.affectedOrders = affectedOrders;
        return this;
    }

    public TransactionSummaryDTO numFilled(Integer numFilled) {
        this.numFilled = numFilled;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof TransactionSummaryDTO)) {
            return false;
        }
        TransactionSummaryDTO transactionSummaryDTO = (TransactionSummaryDTO) o;
        return Objects.equals(affectedOrders, transactionSummaryDTO.affectedOrders) && Objects.equals(numFilled, transactionSummaryDTO.numFilled);
    }

    @Override
    public int hashCode() {
        return Objects.hash(affectedOrders, numFilled);
    }

    @Override
    public String toString() {
        return "{" +
            " affectedOrders='" + getAffectedOrders() + "'" +
            ", numFilled='" + getNumFilled() + "'" +
            "}";
    }

}
