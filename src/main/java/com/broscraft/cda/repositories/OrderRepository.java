package com.broscraft.cda.repositories;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import com.broscraft.cda.database.DB;
import com.broscraft.cda.database.mappers.AffectedOrderMapper;
import com.broscraft.cda.database.mappers.GroupedAskMapper;
import com.broscraft.cda.database.mappers.GroupedBidMapper;
import com.broscraft.cda.database.mappers.PlayerOrderMapper;
import com.broscraft.cda.dtos.items.ItemDTO;
import com.broscraft.cda.dtos.orders.OrderDTO;
import com.broscraft.cda.dtos.orders.OrderType;
import com.broscraft.cda.dtos.orders.grouped.GroupedAskDTO;
import com.broscraft.cda.dtos.orders.grouped.GroupedBidDTO;
import com.broscraft.cda.dtos.orders.grouped.GroupedOrdersDTO;
import com.broscraft.cda.dtos.orders.input.NewOrderDTO;
import com.broscraft.cda.dtos.transaction.TransactionSummaryDTO;
import com.broscraft.cda.utils.EcoUtils;

public class OrderRepository {
    private String emptyOrderQtyStmt;
    private String bestBidStmt;
    private String bestAskStmt;

    public OrderRepository() {
        emptyOrderQtyStmt = (
            "UPDATE Orders " +
            "SET quantity_uncollected = quantity_uncollected + (quantity - quantity_filled), quantity_filled = quantity " +
            "WHERE id IN (?)"
        );

        bestBidStmt = (
            "SELECT MAX(price) " +
            "FROM Orders " +
            "WHERE item_id=? AND type='BID' AND quantity_filled < quantity"
        );

        bestAskStmt = (
            "SELECT MIN(price) " +
            "FROM Orders " +
            "WHERE item_id=? AND type='ASK' AND quantity_filled < quantity"
        );

    }

    public GroupedOrdersDTO getItemOrders(Long itemId) {
        List<GroupedBidDTO> groupedBids = new ArrayList<>();
        List<GroupedAskDTO> groupedAsks = new ArrayList<>(); 
        try (Connection con = DB.getConnection()) {
            PreparedStatement getItemBidsStmt = con.prepareStatement(
                "SELECT price, SUM(quantity - quantity_filled) total_quantity " +
                "FROM Orders " + 
                "WHERE item_id=? AND type='BID' " +
                "GROUP BY price " +
                "HAVING total_quantity > 0 " +
                "ORDER BY price DESC"
            );
            PreparedStatement getItemAsksStmt = con.prepareStatement(
                "SELECT price, SUM(quantity - quantity_filled) total_quantity " +
                "FROM Orders " + 
                "WHERE item_id=? AND type='ASK' " +
                "GROUP BY price " +
                "HAVING total_quantity > 0 " +
                "ORDER BY price ASC"
            );

            getItemBidsStmt.setLong(1, itemId);
            getItemAsksStmt.setLong(1, itemId);

            ResultSet bidResults = getItemBidsStmt.executeQuery();
            ResultSet askResults = getItemAsksStmt.executeQuery();

            GroupedBidMapper groupedBidMapper = new GroupedBidMapper();
            groupedBids = groupedBidMapper.getResults(bidResults);
            bidResults.close();

            GroupedAskMapper groupedAskMapper = new GroupedAskMapper();
            groupedAsks = groupedAskMapper.getResults(askResults);
            askResults.close();

            getItemBidsStmt.close();
            getItemAsksStmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new GroupedOrdersDTO().groupedBids(groupedBids).groupedAsks(groupedAsks);
    }

    public Integer getNumPlayerOrders(UUID playerUUID) {
        Integer numPlayerOrders = null;
        try (Connection con = DB.getConnection()) {
            PreparedStatement getNumPlayerOrdersStmt = con.prepareStatement(
                "SELECT COUNT(*) num_orders " +
                "FROM Orders " +
                "WHERE player_uuid=?"
            );
            getNumPlayerOrdersStmt.setString(1, playerUUID.toString());
            ResultSet rs = getNumPlayerOrdersStmt.executeQuery();
            if (rs.next()) {
                numPlayerOrders = rs.getInt("num_orders");
            }
            rs.close();
            getNumPlayerOrdersStmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return numPlayerOrders;
    }

    public List<OrderDTO> getPlayerOrders(UUID playerUUID) {
        List<OrderDTO> playerOrders = new ArrayList<>();
        
        try (Connection con = DB.getConnection()) {
            PreparedStatement getPlayerOrdersStmt = con.prepareStatement(
                "SELECT id, type, item_id, price, quantity, quantity_filled, quantity_uncollected, quantity - quantity_filled quantity_unfilled " +
                "FROM Orders " +
                "WHERE player_uuid=? " +
                "ORDER BY quantity_uncollected DESC, (quantity_filled = quantity) ASC, created_at DESC"
            );

            getPlayerOrdersStmt.setString(1, playerUUID.toString());
            ResultSet playerOrdersResults = getPlayerOrdersStmt.executeQuery();
            
            PlayerOrderMapper playerOrderMapper = new PlayerOrderMapper();
            while (playerOrdersResults.next()) {
                OrderDTO orderDTO = playerOrderMapper.getRow(playerOrdersResults)
                .playerUUID(playerUUID);

                playerOrders.add(orderDTO);
            }
            playerOrdersResults.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return playerOrders;
    }

    public void createOrder(NewOrderDTO newOrderDTO) {
        try (Connection con = DB.getConnection()) {
            PreparedStatement createOrderStmt = con.prepareStatement(
                "INSERT INTO Orders (type, player_uuid, item_id, price, quantity) " +
                "VALUES (?, ?, ?, ?, ?)"
            );
            createOrderStmt.setString(1, newOrderDTO.getType().toString());
            createOrderStmt.setString(2, newOrderDTO.getPlayerUUID().toString());
            createOrderStmt.setLong(3, newOrderDTO.getItem().getId());
            createOrderStmt.setInt(4, EcoUtils.getValue(newOrderDTO.getPrice()));
            createOrderStmt.setInt(5, newOrderDTO.getQuantity());
            createOrderStmt.executeUpdate();
            createOrderStmt.close();
            con.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public BigDecimal delete(OrderDTO orderDTO) {
        BigDecimal nextBestPrice = null;
        try (Connection con = DB.getConnection()) {
            PreparedStatement deleteOrderStmt = con.prepareStatement(
                "DELETE FROM Orders WHERE id=?"
            );
            deleteOrderStmt.setLong(1, orderDTO.getId());
            deleteOrderStmt.executeUpdate();
            deleteOrderStmt.close();
            con.commit();
            
            PreparedStatement stmt;
            if (orderDTO.getType().equals(OrderType.BID)) {
                stmt = con.prepareStatement(bestBidStmt);
            } else {
                stmt = con.prepareStatement(bestAskStmt);
            }

            stmt.setLong(1, orderDTO.getItem().getId());
            ResultSet results = stmt.executeQuery();
            if (results.next()) {
                int nextBestPriceInt = results.getInt(1);
                if (nextBestPriceInt > 0) nextBestPrice = EcoUtils.parseMoney(nextBestPriceInt);
            }
            results.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nextBestPrice;
    }
    
    public TransactionSummaryDTO fillOrder(OrderType orderType, ItemDTO itemDTO, BigDecimal price, int quantity) {
        TransactionSummaryDTO transactionSummary = new TransactionSummaryDTO();
        List<OrderDTO> affectedOrders = new ArrayList<>();
        int priceValue = EcoUtils.getValue(price);
        try (Connection con = DB.getConnection()) {
            PreparedStatement getOrdersToFillStmt = con.prepareStatement(
                "SELECT id, player_uuid, price, quantity, quantity_filled, quantity_uncollected, quantity - quantity_filled quantity_unfilled " +
                "FROM Orders " +
                "WHERE type=? AND item_id=? " + 
                "AND price=? " +
                "ORDER BY created_at ASC"
            );
    
            Long itemId = Objects.requireNonNull(itemDTO.getId());
            getOrdersToFillStmt.setString(1, orderType.toString());
            getOrdersToFillStmt.setLong(2, itemId);
            getOrdersToFillStmt.setInt(3, priceValue);
            

            int totalAvailable = 0;
            List<Long> toEmptyOrderIds = new ArrayList<>();
            OrderDTO toDecrementOrder = null;
            Integer toDecrementQuantity = null;
            int totalFilled = 0;

            ResultSet orderResults = getOrdersToFillStmt.executeQuery();
            AffectedOrderMapper orderMapper = new AffectedOrderMapper();
            while (orderResults.next() && totalAvailable < quantity) {
                OrderDTO affectedOrder = orderMapper.getRow(orderResults);
                affectedOrder.setType(orderType);
                affectedOrder.setItem(itemDTO);
                
                totalAvailable += affectedOrder.getQuantityUnfilled();
                int overflow = totalAvailable - quantity;
                if (overflow <= 0)  {
                    totalFilled += affectedOrder.getQuantityUnfilled();
                    
                    affectedOrder.setToCollect(affectedOrder.getToCollect() + affectedOrder.getQuantityUnfilled());
                    affectedOrder.setQuantityFilled(affectedOrder.getQuantity());
                    affectedOrder.setQuantityUnfilled(0);
                    
                    toEmptyOrderIds.add(affectedOrder.getId());
                    affectedOrders.add(affectedOrder);
                } else {
                    toDecrementQuantity = quantity - totalFilled;
                    affectedOrder.setToCollect(affectedOrder.getToCollect() + toDecrementQuantity);
                    affectedOrder.setQuantityFilled(affectedOrder.getQuantityFilled() + toDecrementQuantity);
                    affectedOrder.setQuantityUnfilled(affectedOrder.getQuantityUnfilled() - toDecrementQuantity);
                    toDecrementOrder = affectedOrder;
                    totalFilled += toDecrementQuantity;
                    break; // Should exit loop here anyway
                }
            }
            getOrdersToFillStmt.close();
            orderResults.close();
            

            if (toEmptyOrderIds.size() > 0) {
                String idList = toEmptyOrderIds.stream().map(id -> "" + id).collect(Collectors.joining(", "));
                PreparedStatement stmt = con.prepareStatement(emptyOrderQtyStmt.replace("?", idList));
                stmt.executeUpdate();
                stmt.close();
            }

            if (toDecrementOrder != null) {
                PreparedStatement decrementOrderQtyStmt = con.prepareStatement(
                    "UPDATE Orders " +
                    "SET quantity_filled = quantity_filled + ?, quantity_uncollected = quantity_uncollected + ? " +
                    "WHERE id=?"
                );
                affectedOrders.add(toDecrementOrder);
                decrementOrderQtyStmt.setInt(1, toDecrementQuantity);
                decrementOrderQtyStmt.setInt(2, toDecrementQuantity);
                decrementOrderQtyStmt.setLong(3, toDecrementOrder.getId());
                decrementOrderQtyStmt.executeUpdate();
                decrementOrderQtyStmt.close();
            }

            con.commit();

            transactionSummary.setAffectedOrders(affectedOrders);
            transactionSummary.setItemId(itemId);
            transactionSummary.setOrderType(orderType);
            transactionSummary.setNumFilled(totalFilled);

            PreparedStatement getBestPriceStmt;
            if (orderType.equals(OrderType.BID)) getBestPriceStmt = con.prepareStatement(bestBidStmt);
            else getBestPriceStmt = con.prepareStatement(bestAskStmt);

            getBestPriceStmt.setLong(1, itemId);
            ResultSet bestPriceResults = getBestPriceStmt.executeQuery();
            if (bestPriceResults.next()) {
                int nextBestPrice = bestPriceResults.getInt(1);
                if (nextBestPrice > 0) transactionSummary.setNewBestPrice(EcoUtils.parseMoney(nextBestPrice));
            }
            bestPriceResults.close();
            getBestPriceStmt.close();

            return transactionSummary;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void collectOrder(Long orderId, int quantity) {
        try (Connection con = DB.getConnection()) {
            PreparedStatement collectOrderStmt = con.prepareStatement(
                "UPDATE Orders " +
                "SET quantity_uncollected = quantity_uncollected - ? " +
                "WHERE id=?"
            );
            collectOrderStmt.setInt(1, quantity);
            collectOrderStmt.setLong(2, orderId);
            collectOrderStmt.executeUpdate();
            collectOrderStmt.close();
            con.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
