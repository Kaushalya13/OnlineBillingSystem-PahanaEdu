package org.icbt.onlinebillingsystempahanaedu.bill.dao.impl;

import org.icbt.onlinebillingsystempahanaedu.bill.dao.BillDAO;
import org.icbt.onlinebillingsystempahanaedu.bill.entity.BillEntity;
import org.icbt.onlinebillingsystempahanaedu.core.db.DBConnection;
import org.icbt.onlinebillingsystempahanaedu.core.exception.CustomException;
import org.icbt.onlinebillingsystempahanaedu.core.util.DAOUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * author : Niwanthi
 * date : 8/16/2025
 * time : 2:34 PM
 */
public class BillDAOImpl implements BillDAO {
    private static final Logger logger = Logger.getLogger(BillDAOImpl.class.getName());

    @Override
    public int createBill(Connection connection, BillEntity entity) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO bills (customer_id, total_amount) VALUES (?, ?)";

        try (PreparedStatement pst = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pst.setInt(1, entity.getCustomer_id());
            pst.setDouble(2, entity.getTotal_amount());

            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet rs = pst.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (CustomException e) {
            logger.log(Level.SEVERE, "Error while creating bill: " + e.getMessage(), e);
            throw new CustomException(CustomException.ExceptionType.DATABASE_ERROR);
        }
        return -1;
    }

    @Override
    public boolean add(Connection connection, BillEntity entity) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO bills (customer_id, total_amount) VALUES (?, ?)";

        try {
            return DAOUtil.executeUpdate(connection, sql,
                    entity.getCustomer_id(),
                    entity.getTotal_amount()
            );
        } catch (CustomException e){
            logger.log(Level.SEVERE, "Database error while adding Bill: " + e.getMessage(), e);
            throw new CustomException(CustomException.ExceptionType.DATABASE_ERROR);
        }
    }

    @Override
    public List<BillEntity> getAll(Connection connection, Map<String, String> searchParams) throws SQLException, ClassNotFoundException {
        List<BillEntity> bills = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM Bills WHERE deleted_at IS NULL");
        List<Object> params = new ArrayList<>();

        if (searchParams != null && searchParams.containsKey("search")) {
            String searchTerm = "%" + searchParams.get("search") + "%";
            sql.append(" AND customer_id IN (SELECT id FROM customer WHERE cus_Name LIKE ? OR cus_AccountNumber LIKE ?)");
            params.add(searchTerm);
            params.add(searchTerm);
        }

        sql.append(" ORDER BY id ASC");

        ResultSet resultSet = null;

        try {
            resultSet = DAOUtil.executeQuery(connection, sql.toString(), params.toArray());
            while (resultSet.next()) {
                bills.add(mapResultSetToBillEntity(resultSet));
            }
        }finally {
            DBConnection.closeResultSet(resultSet);
        }
        return bills;
    }

    @Override
    public BillEntity searchById(Connection connection, Object... args) throws SQLException, ClassNotFoundException {
        String sql = "SELECT * FROM bills WHERE id = ? AND deleted_at IS NULL";
        Integer id = (Integer) args[0];
        ResultSet resultSet = null;

        try {
            resultSet = DAOUtil.executeQuery(connection, sql, id);
            if (resultSet.next()) {
                return mapResultSetToBillEntity(resultSet);
            }
            return null;
        } finally {
            DBConnection.closeResultSet(resultSet);
        }
    }

    @Override
    public boolean update(Connection connection, BillEntity entity) throws SQLException, ClassNotFoundException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean delete(Connection connection, Object... args) throws SQLException, ClassNotFoundException {
        if (args.length == 0 || args[0] == null) {
            throw new IllegalArgumentException("Bill ID must be provided to delete.");
        }
        Integer billId = (Integer) args[0];
        String sql = "UPDATE bills SET deleted_at = CURRENT_TIMESTAMP WHERE id = ? AND deleted_at IS NULL";
        try {
            return DAOUtil.executeUpdate(connection, sql, billId);
        } catch (RuntimeException e){
            logger.log(Level.SEVERE, "Database error while deleting Bill: " + billId + ":" + e.getMessage(), e);
            throw new CustomException(CustomException.ExceptionType.DATABASE_ERROR);
        }
    }

    @Override
    public int getTotalBills(Connection connection) throws SQLException, ClassNotFoundException {
        String sql = "SELECT COUNT(id) FROM bills WHERE deleted_at IS NULL";

        ResultSet resultSet = null;

        try {
            resultSet = DAOUtil.executeQuery(connection, sql);
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            return 0;
        }finally {
            DBConnection.closeResultSet(resultSet);
        }
    }


    private BillEntity mapResultSetToBillEntity(ResultSet res) throws SQLException {
        BillEntity entity = new BillEntity();
        entity.setId(res.getInt("id"));
        entity.setCustomer_id(res.getInt("customer_id"));
        entity.setTotal_amount(res.getDouble("total_amount"));
        entity.setCreated_at(res.getTimestamp("created_at"));
        entity.setDeleted_at(res.getTimestamp("deleted_at"));

        return entity;
    }

}
