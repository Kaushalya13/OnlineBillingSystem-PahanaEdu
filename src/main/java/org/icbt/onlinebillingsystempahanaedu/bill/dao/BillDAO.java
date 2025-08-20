package org.icbt.onlinebillingsystempahanaedu.bill.dao;

import org.icbt.onlinebillingsystempahanaedu.bill.entity.BillEntity;
import org.icbt.onlinebillingsystempahanaedu.core.repo.CrudDAO;

import java.sql.Connection;
import java.sql.SQLException;

/**
     * author : Niwanthi
     * date : 8/16/2025
     * time : 2:33 PM
 */
public interface BillDAO extends CrudDAO<BillEntity> {
    int createBill(Connection connection, BillEntity entity) throws SQLException, ClassNotFoundException;
    int getTotalBills(Connection connection) throws SQLException, ClassNotFoundException;


}
