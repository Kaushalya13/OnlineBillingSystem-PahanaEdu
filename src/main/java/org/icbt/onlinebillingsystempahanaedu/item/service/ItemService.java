package org.icbt.onlinebillingsystempahanaedu.item.service;

import org.icbt.onlinebillingsystempahanaedu.core.repo.CrudService;
import org.icbt.onlinebillingsystempahanaedu.item.dto.ItemDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * author : Niwanthi
 * date : 8/9/2025
 * time : 1:14 PM
 */
public interface ItemService extends CrudService<ItemDTO> {
    ItemDTO findByName(String name) throws SQLException, ClassNotFoundException;

    List<ItemDTO> getAll(Map<String, String> searchParams) throws SQLException, ClassNotFoundException;

    int getItemsCount() throws SQLException, ClassNotFoundException;

}
