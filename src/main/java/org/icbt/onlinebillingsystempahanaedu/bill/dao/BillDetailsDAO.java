package org.icbt.onlinebillingsystempahanaedu.bill.dao;

import org.icbt.onlinebillingsystempahanaedu.bill.entity.BillDetailsEntity;
import org.icbt.onlinebillingsystempahanaedu.core.repo.CrudDAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * author : Niwanthi
 * date : 8/16/2025
 * time : 2:33 PM
 */
public interface BillDetailsDAO extends CrudDAO<BillDetailsEntity> {
    void saveBillDetails(Connection connection, List<BillDetailsEntity> details) throws SQLException;

    List<BillDetailsEntity> findBillDetailsByBillId(Connection connection, Integer billId) throws SQLException;

}
