package com.broscraft.cda.dtos.items.visitors;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.broscraft.cda.dtos.items.EnchantedItemDTO;
import com.broscraft.cda.dtos.items.ItemDTO;
import com.broscraft.cda.dtos.items.PotionDTO;

public class ItemInserterDB extends ItemVisitor {
    private Long createdKey;
    private PreparedStatement itemStmt;
    private PreparedStatement potionStmt;
    private PreparedStatement enchantmentStmt;

    private Connection con;

    public void setConnection(Connection con) {
        this.con = con;
    }

    public Long getCreatedKey() {
        return this.createdKey;
    }

    public void create(PreparedStatement stmt) {
        try {
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            rs.next();
            this.createdKey = rs.getLong(1);
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void visit(ItemDTO itemDto) {
        try {
            itemStmt = con.prepareStatement("INSERT INTO Items (material) " + "VALUES (?)", Statement.RETURN_GENERATED_KEYS);
            itemStmt.setString(1, itemDto.getMaterial().toString());
            create(itemStmt);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void visit(EnchantedItemDTO enchantedItemDto) {
        visit((ItemDTO) enchantedItemDto); // Create item then attatch enchantments

        try {
            enchantmentStmt = con.prepareStatement("INSERT INTO Enchantments (item_id, enchantment, level) " + "VALUES (?, ?, ?)");
            enchantedItemDto.getEnchantments().forEach(enchantmentDTO -> {
                try {
                    enchantmentStmt.setLong(1, this.createdKey);
                    enchantmentStmt.setString(2, enchantmentDTO.getEnchantment());
                    enchantmentStmt.setInt(3, enchantmentDTO.getLevel());
                    enchantmentStmt.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        } catch (SQLException e1) {
            e1.printStackTrace();
        }

    }

    @Override
    public void visit(PotionDTO potionDto) {
        try {
            potionStmt = con.prepareStatement(
                "INSERT INTO Items (material, potion_type, is_upgraded, is_extended) " +
                "VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS
            );
            potionStmt.setString(1, potionDto.getMaterial().toString());
            potionStmt.setString(2, potionDto.getType().toString());
            potionStmt.setBoolean(3, potionDto.getUpgraded());
            potionStmt.setBoolean(4, potionDto.getExtended());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        create(potionStmt);
    }
    
}
