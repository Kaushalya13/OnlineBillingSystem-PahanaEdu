package org.icbt.onlinebillingsystempahanaedu.item.mapper;

import org.icbt.onlinebillingsystempahanaedu.item.dto.ItemDTO;
import org.icbt.onlinebillingsystempahanaedu.item.entity.ItemEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * author : Niwanthi
 * date : 8/9/2025
 * time : 1:13 PM
 */
public class ItemMapper {
    private ItemMapper(){

    }

    //convert to DTO to Ent
    public static ItemEntity convertItemDTOToItemEntity(ItemDTO itemDTO){
        if (itemDTO == null){
            return null;
        }
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setId(itemDTO.getId());
        itemEntity.setItemName(itemDTO.getItemName());
        itemEntity.setUnitPrice(itemDTO.getUnitPrice());
        itemEntity.setQuantity(itemDTO.getQuantity());
        itemEntity.setCreatedAt(itemDTO.getCreatedAt());
        itemEntity.setUpdatedAt(itemDTO.getUpdatedAt());
        itemEntity.setDeletedAt(itemDTO.getDeletedAt());

        return itemEntity;
    }

    //convert Entity to DTO
    public static ItemDTO convertItemEntityToItemDTO(ItemEntity itemEntity){
        if (itemEntity == null){
            return null;
        }
        ItemDTO itemDTO = new ItemDTO();
        itemDTO.setId(itemEntity.getId());
        itemDTO.setItemName(itemEntity.getItemName());
        itemDTO.setUnitPrice(itemEntity.getUnitPrice());
        itemDTO.setQuantity(itemEntity.getQuantity());
        itemDTO.setCreatedAt(itemEntity.getCreatedAt());
        itemDTO.setUpdatedAt(itemEntity.getUpdatedAt());
        itemDTO.setDeletedAt(itemEntity.getDeletedAt());

        return itemDTO;
    }

    public static List<ItemDTO> convertItemEntityListToItemDTOList(List<ItemEntity> itemEntityList){
        List<ItemDTO> itemDTOList = new ArrayList<>();
        if (itemEntityList != null){
            for (ItemEntity itemEntity : itemEntityList){
                itemDTOList.add(convertItemEntityToItemDTO(itemEntity));
            }
        }
        return itemDTOList;
    }

}
