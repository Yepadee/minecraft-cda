package com.broscraft.cda.database.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.broscraft.cda.dtos.items.EnchantmentDTO;

public class EnchantmentMapper extends RowMapper<EnchantmentDTO> {

    @Override
    public EnchantmentDTO getRow(ResultSet rs) throws SQLException {
        String enchantment = rs.getString("enchantment");
        Integer level = rs.getInt("level");
        EnchantmentDTO enchantmentDTO = new EnchantmentDTO()
        .enchantment(enchantment)
        .level(level);

        return enchantmentDTO;
    }
    
}
