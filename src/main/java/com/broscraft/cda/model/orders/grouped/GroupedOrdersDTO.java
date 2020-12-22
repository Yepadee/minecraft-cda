package com.broscraft.cda.model.orders.grouped;

import java.util.List;
import java.util.Objects;

public class GroupedOrdersDTO {
    private List<GroupedBidDTO> groupedBids;
    private List<GroupedAskDTO> groupedAsks;

    public GroupedOrdersDTO() {
    }

    public GroupedOrdersDTO(List<GroupedBidDTO> groupedBids, List<GroupedAskDTO> groupedAsks) {
        this.groupedBids = groupedBids;
        this.groupedAsks = groupedAsks;
    }

    public List<GroupedBidDTO> getGroupedBids() {
        return this.groupedBids;
    }

    public void setGroupedBids(List<GroupedBidDTO> groupedBids) {
        this.groupedBids = groupedBids;
    }

    public List<GroupedAskDTO> getGroupedAsks() {
        return this.groupedAsks;
    }

    public void setGroupedAsks(List<GroupedAskDTO> groupedAsks) {
        this.groupedAsks = groupedAsks;
    }

    public GroupedOrdersDTO groupedBids(List<GroupedBidDTO> groupedBids) {
        this.groupedBids = groupedBids;
        return this;
    }

    public GroupedOrdersDTO groupedAsks(List<GroupedAskDTO> groupedAsks) {
        this.groupedAsks = groupedAsks;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof GroupedOrdersDTO)) {
            return false;
        }
        GroupedOrdersDTO groupedOrdersDTO = (GroupedOrdersDTO) o;
        return Objects.equals(groupedBids, groupedOrdersDTO.groupedBids) && Objects.equals(groupedAsks, groupedOrdersDTO.groupedAsks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupedBids, groupedAsks);
    }

    @Override
    public String toString() {
        return "{" +
            " groupedBids='" + getGroupedBids() + "'" +
            ", groupedAsks='" + getGroupedAsks() + "'" +
            "}";
    }

}
