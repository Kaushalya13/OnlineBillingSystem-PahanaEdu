package org.icbt.onlinebillingsystempahanaedu.bill.dao.impl;

import org.icbt.onlinebillingsystempahanaedu.bill.dao.BillDetailsDAO;
import org.icbt.onlinebillingsystempahanaedu.bill.entity.BillDetailsEntity;
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
public class BillDetailsDAOImpl implements BillDetailsDAO {
    private static final Logger logger = Logger.getLogger(BillDetailsDAOImpl.class.getName());

    @Override
    public void saveBillDetails(Connection connection, List<BillDetailsEntity> details) throws SQLException {
        String sql = "INSERT INTO bill_details (bill_id, item_id, item_name_at_sale, unit_price_at_sale, units, total) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            for (BillDetailsEntity entity : details) {
                preparedStatement.setInt(1, entity.getBill_id());
                preparedStatement.setInt(2, entity.getItem_id());
                preparedStatement.setString(3, entity.getItem_name_at_sale());
                preparedStatement.setDouble(4, entity.getUnit_price_at_sale());
                preparedStatement.setInt(5, entity.getUnits());
                preparedStatement.setDouble(6, entity.getTotal());

                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        }catch (CustomException e){
            logger.log(Level.SEVERE, "Failed to save bill details", e);
            throw new CustomException(CustomException.ExceptionType.DATABASE_ERROR);
        }
    }

    @Override
    public List<BillDetailsEntity> findBillDetailsByBillId(Connection connection, Integer billId) throws SQLException {
        List<BillDetailsEntity> billDetailsList = new ArrayList<>();
        String sql = "SELECT * FROM bill_details WHERE bill_id=?";
        ResultSet resultSet = null;
        try {
            resultSet = DAOUtil.executeQuery(connection,sql,billId);
            while (resultSet.next()) {
                billDetailsList.add(mapResultSetToEntity(resultSet));
            }
        }finally {
            DBConnection.closeResultSet(resultSet);
        }
        return billDetailsList;
    }

    @Override
    public boolean add(Connection connection, BillDetailsEntity entity) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO bill_details (bill_id,item_id,item_name_at_sale,unit_price_at_sale,units,total) VALUES(?,?,?,?,?,?)";
        try {
            return DAOUtil.executeUpdate(connection,sql,
                    entity.getBill_id(),
                    entity.getItem_id(),
                    entity.getItem_name_at_sale(),
                    entity.getUnit_price_at_sale(),
                    entity.getUnits(),
                    entity.getTotal());
        }catch (CustomException e){
            logger.log(Level.SEVERE,"Failed to add bill details to the database",e);
            throw new CustomException(CustomException.ExceptionType.DATABASE_ERROR);
        }
    }

    @Override
    public List<BillDetailsEntity> getAll(Connection connection, Map<String, String> searchParams) throws SQLException, ClassNotFoundException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public BillDetailsEntity searchById(Connection connection, Object... args) throws SQLException, ClassNotFoundException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean update(Connection connection, BillDetailsEntity entity) throws SQLException, ClassNotFoundException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean delete(Connection connection, Object... args) throws SQLException, ClassNotFoundException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private BillDetailsEntity mapResultSetToEntity(ResultSet resultSet) throws SQLException {
        BillDetailsEntity billDetails = new BillDetailsEntity();
        billDetails.setId(resultSet.getInt("bill_id"));
        billDetails.setBill_id(resultSet.getInt("bill_id"));
        billDetails.setItem_id(resultSet.getInt("item_id"));
        billDetails.setItem_name_at_sale(resultSet.getString("item_name_at_sale"));
        billDetails.setUnit_price_at_sale(resultSet.getDouble("unit_price_at_sale"));
        billDetails.setUnits(resultSet.getInt("units"));
        billDetails.setTotal(resultSet.getDouble("total"));
        billDetails.setCreated_at(resultSet.getTimestamp("created_at"));
        billDetails.setDeleted_at(resultSet.getTimestamp("deleted_at"));

        return billDetails;
    }
}
