package com.broscraft.cda.repositories;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
    
    private PreparedStatement getItemBidsStmt;
    private PreparedStatement getItemAsksStmt;
    private PreparedStatement createOrderStmt;
    private PreparedStatement getOrdersToFillStmt;

    public OrderRepository() {
        getOrdersToFillStmt = DB.prepareStatement(
            "SELECT id, player_uuid, price, quantity, quantity_filled, quantity_uncollected, quantity - quantity_filled " +
            "FROM Orders " +
            "WHERE type=? AND item_id=? " + 
            "AND ? <= price AND price <= ? " +
            "ORDER BY created_at ASC"
        );
        getItemBidsStmt = DB.prepareStatement(
            "SELECT price, SUM(quantity) total_quantity " +
            "FROM Orders " + 
            "WHERE item_id=? AND type='BID' " +
            "GROUP BY price " +
            "ORDER BY price DESC"
        );
        getItemAsksStmt = DB.prepareStatement(
            "SELECT price, SUM(quantity) total_quantity " +
            "FROM Orders " + 
            "WHERE item_id=? AND type='ASK' " +
            "GROUP BY price " +
            "ORDER BY price ASC"
        );
        createOrderStmt = DB.prepareStatement(
            "INSERT INTO Orders (type, player_uuid, item_id, price, quantity) " +
            "VALUES (?, ?, ?, ?, ?)"
        );
    }

    public GroupedOrdersDTO getItemOrders(Long itemId) {
        List<GroupedBidDTO> groupedBids = new ArrayList<>();
        List<GroupedAskDTO> groupedAsks = new ArrayList<>();

        try {
            getItemBidsStmt.setLong(1, itemId);
            ResultSet bidResults = DB.query(getItemBidsStmt);
            while (bidResults.next()) {
                float price = bidResults.getFloat(1);
                int quantity = bidResults.getInt(2);
                GroupedBidDTO groupedBid = new GroupedBidDTO();
                groupedBid.price(price).quantity(quantity);
                groupedBids.add(groupedBid);   
            }
            bidResults.close();

            getItemAsksStmt.setLong(1, itemId);
            ResultSet askResults = DB.query(getItemAsksStmt);
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
        
        try {
            createOrderStmt.setString(1, newOrderDTO.getType().toString());
            createOrderStmt.setString(2, newOrderDTO.getPlayerUUID().toString());
            createOrderStmt.setLong(3, newOrderDTO.getItem().getId());
            createOrderStmt.setFloat(4, newOrderDTO.getPrice());
            createOrderStmt.setInt(5, newOrderDTO.getQuantity());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        DB.update(createOrderStmt);
        DB.commit();
    }

    public Float delete(Long orderId) {
        // TODO: submit delete request
        // TODO: return next best price
        return 0.0f;
    }
    // TODO: cache prepared statements
    public TransactionSummaryDTO fillOrder(OrderType orderType, ItemDTO itemDTO, float price, int quantity) {
        TransactionSummaryDTO transactionSummary = new TransactionSummaryDTO();
        List<OrderDTO> affectedOrders = new ArrayList<>();
 
        float minPrice = price - EPSILON;
        float maxPrice = price + EPSILON;

        System.out.println(minPrice);
        System.out.println(price);
        System.out.println(maxPrice);
        try {
            Long itemId = Objects.requireNonNull(itemDTO.getId());
            getOrdersToFillStmt.setString(1, orderType.toString());
            getOrdersToFillStmt.setLong(2, itemId);
            getOrdersToFillStmt.setFloat(3, minPrice);
            getOrdersToFillStmt.setFloat(4, maxPrice);
            ResultSet orderResults = DB.query(getOrdersToFillStmt);

            int totalAvailable = 0;
            List<Long> toEmptyOrderIds = new ArrayList<>();
            Long toDecrementOrderId = null;
            int totalFilled = 0;
            System.out.println("q: " + quantity);
            while (orderResults.next() && totalAvailable < quantity) {
                OrderDTO affectedOrder = new OrderDTO();
                affectedOrder.setType(orderType);
                affectedOrder.setId(orderResults.getLong(1));
                affectedOrder.setPlayerUUID(UUID.fromString(orderResults.getString(2)));
                affectedOrder.setPrice(orderResults.getFloat(3));
                affectedOrder.setQuantity(orderResults.getInt(4));
                affectedOrder.setQuantityFilled(orderResults.getInt(5));
                affectedOrder.setToCollect(orderResults.getInt(6));
                affectedOrder.setQuantityUnfilled(orderResults.getInt(7));
                affectedOrder.setItem(itemDTO);
                
                
                totalAvailable += affectedOrder.getQuantityUnfilled();
                int overflow = totalAvailable - quantity;
                
                if (overflow <= 0)  {
                    totalFilled += affectedOrder.getQuantityUnfilled();
                    
                    affectedOrder.setToCollect(affectedOrder.getToCollect() + affectedOrder.getQuantityUnfilled());
                    affectedOrder.setQuantityFilled(affectedOrder.getQuantity());
                    affectedOrder.setQuantityUnfilled(0);
                    
                    toEmptyOrderIds.add(affectedOrder.getId());
                } else {
                    int remainder = quantity - totalFilled;
                    System.out.println("r: " + remainder);
                    affectedOrder.setToCollect(affectedOrder.getToCollect() + remainder);
                    affectedOrder.setQuantityFilled(affectedOrder.getQuantityFilled() + remainder);
                    affectedOrder.setQuantityUnfilled(affectedOrder.getQuantityUnfilled() - remainder);
                    toDecrementOrderId = affectedOrder.getId();
                    totalFilled += remainder;
                }

                affectedOrders.add(affectedOrder);
            }

            toEmptyOrderIds.forEach(order -> {
                int newQuantity;
            });
            
            transactionSummary.setAffectedOrders(affectedOrders);
            transactionSummary.setNumFilled(totalFilled);
            System.out.println(transactionSummary);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return transactionSummary;
    }

    public void collectOrder(Long orderId, int quantity) {

    }
}
