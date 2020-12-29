package com.broscraft.cda.repositories;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.broscraft.cda.database.DB;
import com.broscraft.cda.dtos.items.ItemDTO;
import com.broscraft.cda.dtos.orders.BestPriceDTO;
import com.broscraft.cda.dtos.orders.OrderDTO;
import com.broscraft.cda.dtos.orders.OrderType;
import com.broscraft.cda.dtos.orders.grouped.GroupedAskDTO;
import com.broscraft.cda.dtos.orders.grouped.GroupedBidDTO;
import com.broscraft.cda.dtos.orders.grouped.GroupedOrdersDTO;
import com.broscraft.cda.dtos.orders.input.NewOrderDTO;
import com.broscraft.cda.dtos.transaction.TransactionSummaryDTO;

import org.bukkit.Material;

public class OrderRepository {
    public GroupedOrdersDTO getItemOrders(Long itemId) {
        // TODO: Actually load orders for the item
        System.out.println("Loading orders for item " + itemId + "!");
        List<GroupedBidDTO> bids = new ArrayList<>();
        List<GroupedAskDTO> asks = new ArrayList<>();
        for (int i = 1; i <= 15; ++i) {
            GroupedBidDTO bid1 = new GroupedBidDTO();
            bid1.setPrice(3.0f / i);
            bid1.setQuantity(100 / i);
            bids.add(bid1);

            GroupedAskDTO ask1 = new GroupedAskDTO();
            ask1.setPrice(i * 3.0f + 0.1f);
            ask1.setQuantity(120 / i);
            asks.add(ask1);
        }

        GroupedAskDTO ask1 = new GroupedAskDTO();
        ask1.setPrice(100.0f);
        ask1.setQuantity(120);
        asks.add(ask1);

        return new GroupedOrdersDTO().groupedBids(bids).groupedAsks(asks);
    }

    public List<OrderDTO> getPlayerOrders(UUID playerUUID) {
        // TODO: Actually load orders
        System.out.println("Loading orders for player " + playerUUID + "!");
        List<OrderDTO> orderDTOs = new ArrayList<>();
        orderDTOs.add(new OrderDTO().id(1L).type(OrderType.ASK).price(10.3f).quantity(3).quantityFilled(1)
                .playerUUID(UUID.fromString("ff5b7624-5859-455e-b708-e7cb227e114d")).toCollect(2)
                .item(new ItemDTO().id(1L).material(Material.STONE)));

        orderDTOs.add(new OrderDTO().id(2L).type(OrderType.BID).price(5.5f).quantity(3).quantityFilled(2)
                .playerUUID(UUID.fromString("ff5b7624-5859-455e-b708-e7cb227e114d")).toCollect(1)
                .item(new ItemDTO().id(2L).material(Material.DIAMOND_BLOCK)));

        return orderDTOs;
    }

    public void createOrder(NewOrderDTO newOrderDTO) {
        System.out.println("Created new order:" + newOrderDTO);
        PreparedStatement stmt = DB.prepareStatement(
            "INSERT INTO Orders (type, player_uuid, item_id, price, quantity) " +
            "VALUES (?, ?, ?, ?, ?)"
        );
        try {
            stmt.setString(1, newOrderDTO.getType().toString());
            stmt.setString(2, newOrderDTO.getPlayerUUID().toString());
            stmt.setLong(3, newOrderDTO.getItem().getId());
            stmt.setFloat(4, newOrderDTO.getPrice());
            stmt.setInt(5, newOrderDTO.getQuantity());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        DB.update(stmt);
        DB.commit();
    }

    public BestPriceDTO getBestPrice(Long itemId, OrderType orderType) {
        if (itemId == null) return null;
        String minMax = "MIN";
        if (orderType.equals(OrderType.BID)) minMax = "MAX";
        System.out.println(minMax);
        PreparedStatement stmt = DB.prepareStatement(
            "SELECT " + minMax +"(b.price) price, b.quantity " +
            "FROM Orders a " +
            "INNER JOIN ( " +
               "SELECT id,  price, SUM(quantity) quantity " +
                "FROM Orders  " +
                "WHERE item_id=? AND type=?  " +
                "GROUP BY price " +
            ") b ON a.id = b.id"
        );

        try {
            stmt.setLong(1, itemId);
            stmt.setString(2, orderType.toString());

            ResultSet results = DB.query(stmt);
            
            if (results.next()) {
                BestPriceDTO bestPriceDTO = new BestPriceDTO();
                bestPriceDTO.setPrice(results.getFloat(1));
                bestPriceDTO.setQuantity(results.getInt(2));
                System.out.println(bestPriceDTO);
                return bestPriceDTO;
            } else {
                return null;
            }  
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void delete(Long orderId) {
        // TODO: submit delete request
    }

    public TransactionSummaryDTO fillOrder(Long itemId, float price, int quantity) {
        TransactionSummaryDTO transactionSummary = new TransactionSummaryDTO();
        // TODO: send request to fill order and retrieve affected orders
        List<OrderDTO> affectedOrders = new ArrayList<>();

        transactionSummary.setAffectedOrders(affectedOrders);
        transactionSummary.setNumFilled(quantity);
        return transactionSummary;
    }

    public void collectOrder(Long orderId, int quantity) {

    }
}
