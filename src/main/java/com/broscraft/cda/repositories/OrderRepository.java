package com.broscraft.cda.repositories;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import com.broscraft.cda.database.DB;
import com.broscraft.cda.dtos.items.ItemDTO;
import com.broscraft.cda.dtos.orders.OrderDTO;
import com.broscraft.cda.dtos.orders.OrderType;
import com.broscraft.cda.dtos.orders.grouped.GroupedAskDTO;
import com.broscraft.cda.dtos.orders.grouped.GroupedBidDTO;
import com.broscraft.cda.dtos.orders.grouped.GroupedOrdersDTO;
import com.broscraft.cda.dtos.orders.input.NewOrderDTO;
import com.broscraft.cda.dtos.transaction.TransactionSummaryDTO;

public class OrderRepository {

    private static float EPSILON = 0.001f;
    
    private PreparedStatement getItemBidsStmt;
    private PreparedStatement getItemAsksStmt;
    private PreparedStatement createOrderStmt;

    private PreparedStatement getOrdersToFillStmt;
    private String emptyOrderQtyStmt;
    private PreparedStatement decrementOrderQtyStmt;

    private PreparedStatement getPlayerOrdersStmt;

    private PreparedStatement collectOrderStmt;
    private PreparedStatement deleteOrderStmt;

    public OrderRepository() {
        getOrdersToFillStmt = DB.prepareStatement(
            "SELECT id, player_uuid, price, quantity, quantity_filled, quantity_uncollected, quantity - quantity_filled quantity_unfilled " +
            "FROM Orders " +
            "WHERE type=? AND item_id=? " + 
            "AND ? <= price AND price <= ? " +
            "ORDER BY created_at ASC"
        );
        getItemBidsStmt = DB.prepareStatement(
            "SELECT price, SUM(quantity - quantity_filled) total_quantity " +
            "FROM Orders " + 
            "WHERE item_id=? AND type='BID' " +
            "GROUP BY price " +
            "HAVING total_quantity > 0 " +
            "ORDER BY price DESC"
        );
        getItemAsksStmt = DB.prepareStatement(
            "SELECT price, SUM(quantity - quantity_filled) total_quantity " +
            "FROM Orders " + 
            "WHERE item_id=? AND type='ASK' " +
            "GROUP BY price " +
            "HAVING total_quantity > 0 " +
            "ORDER BY price ASC"
        );
        createOrderStmt = DB.prepareStatement(
            "INSERT INTO Orders (type, player_uuid, item_id, price, quantity) " +
            "VALUES (?, ?, ?, ?, ?)"
        );
        emptyOrderQtyStmt = 
            "UPDATE Orders " +
            "SET quantity_filled = quantity, quantity_uncollected = quantity_uncollected + (quantity - quantity_filled) " +
            "WHERE id IN (?)"
        ;
        decrementOrderQtyStmt = DB.prepareStatement(
            "UPDATE Orders " +
            "SET quantity_filled = quantity_filled + ?, quantity_uncollected = quantity_uncollected + ? " +
            "WHERE id=?"
        );
        getPlayerOrdersStmt = DB.prepareStatement(
            "SELECT id, type, item_id, price, quantity, quantity_filled, quantity_uncollected, quantity - quantity_filled quantity_unfilled " +
            "FROM Orders " +
            "WHERE player_uuid=?"
        );

        collectOrderStmt = DB.prepareStatement(
            "UPDATE Orders " +
            "SET quantity_uncollected = quantity_uncollected - ? " +
            "WHERE id=?"
        );
        
    }

    public GroupedOrdersDTO getItemOrders(Long itemId) {
        List<GroupedBidDTO> groupedBids = new ArrayList<>();
        List<GroupedAskDTO> groupedAsks = new ArrayList<>();

        try {
            getItemBidsStmt.setLong(1, itemId);
            ResultSet bidResults = getItemBidsStmt.executeQuery();
            while (bidResults.next()) {
                float price = bidResults.getFloat(1);
                int quantity = bidResults.getInt(2);
                GroupedBidDTO groupedBid = new GroupedBidDTO();
                groupedBid.price(price).quantity(quantity);
                groupedBids.add(groupedBid);   
            }
            bidResults.close();

            getItemAsksStmt.setLong(1, itemId);
            ResultSet askResults = getItemAsksStmt.executeQuery();
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
        List<OrderDTO> playerOrders = new ArrayList<>();
        
        try {
            getPlayerOrdersStmt.setString(1, playerUUID.toString());
            ResultSet results = getPlayerOrdersStmt.executeQuery();
        
            while (results.next()) {
                OrderDTO orderDTO = new OrderDTO();
                orderDTO.id(results.getLong(1))
                .type(OrderType.valueOf(results.getString(2)))
                .playerUUID(playerUUID)
                .item(new ItemDTO().id(results.getLong(3)))
                .price(results.getFloat(4))
                .quantity(results.getInt(5))
                .quantityFilled(results.getInt(6))
                .toCollect(results.getInt(7))
                .quantityUnfilled(results.getInt(8));
                playerOrders.add(orderDTO);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return playerOrders;
    }

    public void createOrder(NewOrderDTO newOrderDTO) {
        System.out.println("Created new order:" + newOrderDTO);
        
        try {
            createOrderStmt.setString(1, newOrderDTO.getType().toString());
            createOrderStmt.setString(2, newOrderDTO.getPlayerUUID().toString());
            createOrderStmt.setLong(3, newOrderDTO.getItem().getId());
            createOrderStmt.setFloat(4, newOrderDTO.getPrice());
            createOrderStmt.setInt(5, newOrderDTO.getQuantity());
            createOrderStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
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
        try {
            Long itemId = Objects.requireNonNull(itemDTO.getId());
            getOrdersToFillStmt.setString(1, orderType.toString());
            getOrdersToFillStmt.setLong(2, itemId);
            getOrdersToFillStmt.setFloat(3, minPrice);
            getOrdersToFillStmt.setFloat(4, maxPrice);
            ResultSet orderResults = getOrdersToFillStmt.executeQuery();

            int totalAvailable = 0;
            List<Long> toEmptyOrderIds = new ArrayList<>();
            Long toDecrementOrderId = null;
            Integer toDecrementQuantity = null;
            int totalFilled = 0;
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
                    toDecrementQuantity = quantity - totalFilled;
                    affectedOrder.setToCollect(affectedOrder.getToCollect() + toDecrementQuantity);
                    affectedOrder.setQuantityFilled(affectedOrder.getQuantityFilled() + toDecrementQuantity);
                    affectedOrder.setQuantityUnfilled(affectedOrder.getQuantityUnfilled() - toDecrementQuantity);
                    toDecrementOrderId = affectedOrder.getId();
                    totalFilled += toDecrementQuantity;
                    break; // Should exit loop here anyway
                }

                affectedOrders.add(affectedOrder);
            }

            if (toEmptyOrderIds.size() > 0) {
                String idList = toEmptyOrderIds.stream().map(id -> "" + id).collect(Collectors.joining(", "));
                String stmt = emptyOrderQtyStmt.replace("?", idList);
                DB.update(stmt);
            }

            if (toDecrementOrderId != null) {
                decrementOrderQtyStmt.setInt(1, toDecrementQuantity);
                decrementOrderQtyStmt.setInt(2, toDecrementQuantity);
                decrementOrderQtyStmt.setLong(3, toDecrementOrderId);
                decrementOrderQtyStmt.executeUpdate();
            }
            
            transactionSummary.setAffectedOrders(affectedOrders);
            transactionSummary.setNumFilled(totalFilled);
            DB.commit();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return transactionSummary;
    }

    public void collectOrder(Long orderId, int quantity) {
        try {
            collectOrderStmt.setInt(1, quantity);
            collectOrderStmt.setLong(2, orderId);
            collectOrderStmt.executeUpdate();
            DB.commit();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
