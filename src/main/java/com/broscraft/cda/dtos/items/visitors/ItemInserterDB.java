package com.broscraft.cda.dtos.items.visitors;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.broscraft.cda.database.DB;
import com.broscraft.cda.dtos.items.EnchantedItemDTO;
import com.broscraft.cda.dtos.items.ItemDTO;
import com.broscraft.cda.dtos.items.PotionDTO;

public class ItemInserterDB extends ItemVisitor {
    private Long createdKey;
    PreparedStatement itemStmt;
    PreparedStatement potionStmt;
    PreparedStatement enchantmentStmt;

    public ItemInserterDB() {
        itemStmt = DB.prepareStatement("INSERT INTO Items (material) " + "VALUES (?)");
        potionStmt = DB.prepareStatement(
            "INSERT INTO Items (material, potion_type, is_upgraded, is_extended) " +
            "VALUES (?, ?, ?, ?)"
        );
        enchantmentStmt = DB.prepareStatement(
            "INSERT INTO Enchantments (item_id, enchantment, level) " +
            "VALUES (?, ?, ?)"
        );
    }

    public Long getCreatedKey() {
        return this.createdKey;
    }

    @Override
    public void visit(ItemDTO itemDto) {
        try {
            itemStmt.setString(1, itemDto.getMaterial().toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.createdKey = DB.create(itemStmt);
        itemDto.setId(createdKey);
    }

    @Override
    public void visit(EnchantedItemDTO enchantedItemDto) {
        visit((ItemDTO) enchantedItemDto);
        enchantedItemDto.getEnchantments().forEach(enchantmentDTO -> {
            try {
                enchantmentStmt.setLong(1, enchantedItemDto.getId());
                enchantmentStmt.setString(2, enchantmentDTO.getEnchantment());
                enchantmentStmt.setInt(3, enchantmentDTO.getLevel());
                enchantmentStmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            
        });

    }

    @Override
    public void visit(PotionDTO potionDto) {
        try {
            potionStmt.setString(1, potionDto.getMaterial().toString());
            potionStmt.setString(2, potionDto.getType().toString());
            potionStmt.setBoolean(3, potionDto.getUpgraded());
            potionStmt.setBoolean(4, potionDto.getExtended());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        this.createdKey = DB.create(potionStmt);
    }
    
}
