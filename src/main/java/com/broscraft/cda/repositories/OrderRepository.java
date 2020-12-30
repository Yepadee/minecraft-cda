package com.broscraft.cda.repositories;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.broscraft.cda.database.DB;
import com.broscraft.cda.dtos.items.ItemDTO;
import com.broscraft.cda.dtos.orders.OrderDTO;
import com.broscraft.cda.dtos.orders.OrderType;
import com.broscraft.cda.dtos.orders.grouped.GroupedAskDTO;
import com.broscraft.cda.dtos.orders.grouped.GroupedBidDTO;
import com.broscraft.cda.dtos.orders.grouped.GroupedOrdersDTO;
import com.broscraft.cda.dtos.orders.input.NewOrderDTO;
import com.broscraft.cda.dtos.transaction.TransactionSummaryDTO;

import org.bukkit.Material;

public class OrderRepository {

    private static float EPSILON = 0.001f;

    public GroupedOrdersDTO getItemOrders(Long itemId) {
        PreparedStatement bidStmt = DB.prepareStatement(
            "SELECT price, SUM(quantity) total_quantity " +
            "FROM Orders " + 
            "WHERE item_id=? AND type='BID' " +
            "GROUP BY price " +
            "ORDER BY price DESC"
        );

        PreparedStatement askStmt = DB.prepareStatement(
            "SELECT price, SUM(quantity) total_quantity " +
            "FROM Orders " + 
            "WHERE item_id=? AND type='ASK' " +
            "GROUP BY price " +
            "ORDER BY price ASC"
        );

        List<GroupedBidDTO> groupedBids = new ArrayList<>();
        List<GroupedAskDTO> groupedAsks = new ArrayList<>();

        try {
            bidStmt.setLong(1, itemId);
            ResultSet bidResults = DB.query(bidStmt);
            while (bidResults.next()) {
                float price = bidResults.getFloat(1);
                int quantity = bidResults.getInt(2);
                GroupedBidDTO groupedBid = new GroupedBidDTO();
                groupedBid.price(price).quantity(quantity);
                groupedBids.add(groupedBid);   
            }
            bidResults.close();

            askStmt.setLong(1, itemId);
            ResultSet askResults = DB.query(askStmt);
            while (askResults.next()) {
                float price = askResults.getFloat(1);
                int quantity = askResults.getInt(2);
                GroupedAskDTO groupedAsk = new GroupedAskDTO();
                groupedAsk.price(price).quantity(quantity);
                groupedAsks.add(groupedAsk);   
            }
            askResults.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new GroupedOrdersDTO().groupedBids(groupedBids).groupedAsks(groupedAsks);
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
        try {
            stmt.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public Float delete(Long orderId) {
        // TODO: submit delete request
        // TODO: return next best price
        return 0.0f;
    }

    public TransactionSummaryDTO fillOrder(OrderType orderType, Long itemId, float price, int quantity) {
        TransactionSummaryDTO transactionSummary = new TransactionSummaryDTO();
        // TODO: send request to fill order and retrieve affected orders
        List<OrderDTO> affectedOrders = new ArrayList<>();
        

        PreparedStatement getStmt = DB.prepareStatement(
            "SELECT id, price, quantity, quantity_filled, quantity_uncollected, quanitity - quanitity_filled " +
            "FROM Orders " +
            "WHERE type=? AND item_id=? " + 
            "AND ? <= price AND price <= ? " +
            "ORDER BY created_at ASC"
        );
        float minPrice = price - EPSILON;
        float maxPrice = price + EPSILON;
        try {
            getStmt.setString(1, orderType.toString());
            getStmt.setLong(2, itemId);
            getStmt.setFloat(3, minPrice);
            getStmt.setFloat(4, maxPrice);
            ResultSet orderResults = DB.query(getStmt);
            int totalAvailable = 0;
            while (orderResults.next() && totalAvailable < quantity) {
                OrderDTO affectedOrder = new OrderDTO();
                affectedOrder.setId(orderResults.getLong(1));
                affectedOrder.setPrice(orderResults.getFloat(2));
                affectedOrder.setQuantity(orderResults.getInt(3));
                affectedOrder.setQuantityFilled(orderResults.getInt(4));
                affectedOrder.setToCollect(orderResults.getInt(5));

                affectedOrders.add(affectedOrder);
                totalAvailable += orderResults.getInt(6);
            }

            

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        transactionSummary.setAffectedOrders(affectedOrders);
        transactionSummary.setNumFilled(quantity);
        return transactionSummary;
    }

    public void collectOrder(Long orderId, int quantity) {

    }
}
