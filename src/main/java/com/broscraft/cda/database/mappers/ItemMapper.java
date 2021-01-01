package com.broscraft.cda.database.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.broscraft.cda.dtos.items.ItemDTO;
import com.broscraft.cda.dtos.items.PotionDTO;

import org.bukkit.Material;
import org.bukkit.potion.PotionType;

public class ItemMapper extends RowMapper<ItemDTO> {

    @Override
    public ItemDTO getRow(ResultSet rs) throws SQLException {
        long itemId = rs.getLong("item_id");
        Material material = Material.valueOf(rs.getString("material"));
        String potionType = rs.getString("potion_type");
        Boolean upgraded = rs.getBoolean("is_upgraded");
        Boolean extended = rs.getBoolean("is_extended");

        ItemDTO itemDTO;
        if (potionType == null) {
            itemDTO = new ItemDTO();
        } else {
            PotionDTO potionDTO = new PotionDTO();
            potionDTO.setType(PotionType.valueOf(potionType));
            potionDTO.upgraded(upgraded)
            .extended(extended);
            itemDTO = potionDTO;
        }
         
        itemDTO.id(itemId);
        itemDTO.material(material);

        return itemDTO;
    }
    
}
