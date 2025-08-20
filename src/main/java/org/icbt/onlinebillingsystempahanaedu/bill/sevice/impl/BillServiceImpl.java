package org.icbt.onlinebillingsystempahanaedu.bill.sevice.impl;

import org.icbt.onlinebillingsystempahanaedu.bill.dao.BillDAO;
import org.icbt.onlinebillingsystempahanaedu.bill.dao.BillDetailsDAO;
import org.icbt.onlinebillingsystempahanaedu.bill.dao.impl.BillDAOImpl;
import org.icbt.onlinebillingsystempahanaedu.bill.dao.impl.BillDetailsDAOImpl;
import org.icbt.onlinebillingsystempahanaedu.bill.dto.BillDTO;
import org.icbt.onlinebillingsystempahanaedu.bill.dto.BillDetailsDTO;
import org.icbt.onlinebillingsystempahanaedu.bill.entity.BillDetailsEntity;
import org.icbt.onlinebillingsystempahanaedu.bill.entity.BillEntity;
import org.icbt.onlinebillingsystempahanaedu.bill.mapper.BillMapper;
import org.icbt.onlinebillingsystempahanaedu.bill.sevice.BillService;
import org.icbt.onlinebillingsystempahanaedu.core.db.DBConnection;
import org.icbt.onlinebillingsystempahanaedu.core.exception.CustomException;
import org.icbt.onlinebillingsystempahanaedu.customer.dao.CustomerDAO;
import org.icbt.onlinebillingsystempahanaedu.customer.dao.impl.CustomerDAOImpl;
import org.icbt.onlinebillingsystempahanaedu.customer.entity.CustomerEntity;
import org.icbt.onlinebillingsystempahanaedu.item.dao.ItemDAO;
import org.icbt.onlinebillingsystempahanaedu.item.dao.impl.ItemDAOImpl;
import org.icbt.onlinebillingsystempahanaedu.item.entity.ItemEntity;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * author : Niwanthi
 * date : 8/16/2025
 * time : 2:39 PM
 */
public class BillServiceImpl implements BillService {
    private final BillDAO billDAO;
    private final BillDetailsDAO billDetailsDAO;
    private final ItemDAO itemDAO;
    private final CustomerDAO customerDAO;

    private static final Logger logger = Logger.getLogger(BillServiceImpl.class.getName());

    public BillServiceImpl() {
        this.billDAO = new BillDAOImpl();
        this.billDetailsDAO = new BillDetailsDAOImpl();
        this.itemDAO = new ItemDAOImpl();
        this.customerDAO = new CustomerDAOImpl();
    }

    @Override
    public BillDTO createBill(BillDTO billDTO, Integer generatedByUserId) throws ClassNotFoundException {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            connection.setAutoCommit(false);

            logger.log(Level.INFO, "Attempting to generate bill for customer ID: " + billDTO.getCustomer_id());

            CustomerEntity customerEntity = customerDAO.searchById(connection, billDTO.getCustomer_id());
            if (customerEntity == null) {
                throw new CustomException(CustomException.ExceptionType.CUSTOMER_NOT_FOUND);
            }

            if (billDTO.getBillDetails() == null || billDTO.getBillDetails().isEmpty()) {
                throw new CustomException(CustomException.ExceptionType.INVALID_BILL_INPUTS);
            }

            int totalUnits = 0;

            for (BillDetailsDTO detail : billDTO.getBillDetails()) {
                ItemEntity item = itemDAO.searchById(connection, detail.getItem_id());
                if (item == null || item.getDeletedAt() != null) {
                    throw new CustomException(CustomException.ExceptionType.ITEM_NOT_FOUND);
                }

                if (item.getQuantity() < detail.getUnits()) {
                    throw new CustomException(CustomException.ExceptionType.INSUFFICIENT_STOCK);
                }

                detail.setItem_name_at_sale(item.getItemName());
                detail.setUnit_price_at_sale(item.getUnitPrice());
                detail.setTotal(item.getUnitPrice() * detail.getUnits());

                item.setQuantity(item.getQuantity() - detail.getUnits());
                if (!itemDAO.update(connection, item)) {
                    throw new CustomException(CustomException.ExceptionType.INTERNAL_SERVER_ERROR);
                }

                totalUnits += detail.getUnits();
            }

            logger.log(Level.INFO, "Updating customer units. Adding: " + totalUnits);
            customerEntity.setUnits_consumed(customerEntity.getUnits_consumed() + totalUnits);
            if (!customerDAO.update(connection, customerEntity)) {
                throw new CustomException(CustomException.ExceptionType.CUSTOMER_UPDATE_FAILED);
            }

            Double totalAmount = billDTO.getBillDetails().stream()
                    .map(BillDetailsDTO::getTotal)
                    .reduce(0.0, Double::sum);
            billDTO.setTotal_amount(totalAmount);

            BillEntity billEntity = BillMapper.toEntity(billDTO);
            int addedBillId = billDAO.createBill(connection, billEntity);
            if (addedBillId == -1) {
                throw new CustomException(CustomException.ExceptionType.INTERNAL_SERVER_ERROR);
            }
            billDTO.setId(addedBillId);


            List<BillDetailsEntity> billDetailEntities = new ArrayList<>();
            for (BillDetailsDTO detail : billDTO.getBillDetails()) {
                BillDetailsEntity billDetailsEntity = BillMapper.toEntity(detail);
                billDetailsEntity.setBill_id(addedBillId);
                billDetailEntities.add(billDetailsEntity);
            }
            billDetailsDAO.saveBillDetails(connection, billDetailEntities);

            connection.commit();
            logger.log(Level.INFO, "Bill ID " + addedBillId + " generated successfully.");

            return findBillById(addedBillId);

        } catch (SQLException | ClassNotFoundException e) {
            DBConnection.rollback(connection);
            logger.log(Level.SEVERE, "Database error during bill generation: " + e.getMessage(), e);
            throw new CustomException(CustomException.ExceptionType.DATABASE_ERROR);
        } catch (CustomException e) {
            DBConnection.rollback(connection);
            logger.log(Level.WARNING, "Business error during bill generation: " + e.getExceptionType().name() + " - " + e.getMessage());
            throw e;
        } finally {
            DBConnection.closeConnection(connection);
        }
    }

    @Override
    public boolean deleteBill(Integer deletedByUserId, Integer billId) throws ClassNotFoundException {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            return billDAO.delete(connection, billId);
        } catch (SQLException | ClassNotFoundException e) {
            logger.log(Level.SEVERE, "Error deleting bill with ID: " + billId, e);
            throw new CustomException(CustomException.ExceptionType.DATABASE_ERROR);
        } finally {
            DBConnection.closeConnection(connection);
        }
    }

    @Override
    public BillDTO findBillById(Integer billId) throws ClassNotFoundException {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            BillEntity billEntity = billDAO.searchById(connection, billId);
            if (billEntity == null) {
                return null;
            }

            BillDTO billDTO = BillMapper.toDTO(billEntity);

            List<BillDetailsEntity> detailsEntities = billDetailsDAO.findBillDetailsByBillId(connection, billId);
            billDTO.setBillDetails(BillMapper.ToDTOListDetails(detailsEntities));

            enrichBillDTO(connection, billDTO);

            return billDTO;
        } catch (SQLException | ClassNotFoundException e) {
            logger.log(Level.SEVERE, "Error finding bill by ID: " + billId, e);
            throw new CustomException(CustomException.ExceptionType.DATABASE_ERROR);
        } finally {
            DBConnection.closeConnection(connection);
        }
    }

    @Override
    public List<BillDTO> findAllBills(Map<String, String> searchParams) throws ClassNotFoundException {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            List<BillEntity> billEntities = billDAO.getAll(connection, searchParams);
            List<BillDTO> billDTOs = BillMapper.toDTOList(billEntities);

            for (BillDTO billDTO : billDTOs) {
                enrichBillDTO(connection, billDTO);
            }
            return billDTOs;
        } catch (SQLException | ClassNotFoundException e) {
            logger.log(Level.SEVERE, "Error finding all bills", e);
            throw new CustomException(CustomException.ExceptionType.DATABASE_ERROR);
        } finally {
            DBConnection.closeConnection(connection);
        }
    }

    @Override
    public int getTotalBills() throws SQLException, ClassNotFoundException {
        Connection connection = null;

        try {
            connection = DBConnection.getConnection();
            return billDAO.getTotalBills(connection);
        }catch (SQLException e) {
            logger.log(Level.SEVERE,"Database Exception: " + e.getMessage());
            throw new CustomException(CustomException.ExceptionType.DATABASE_ERROR);
        }finally {
            DBConnection.closeConnection(connection);
        }
    }

    private void enrichBillDTO(Connection connection, BillDTO billDTO) throws SQLException, ClassNotFoundException {
        if (billDTO == null) return;
        CustomerEntity customer = customerDAO.searchById(connection, billDTO.getCustomer_id());
        if (customer != null) {
            billDTO.setCus_Name(customer.getCus_Name());
            billDTO.setCus_AccountNumber(customer.getCus_AccountNumber());
        } else {
            billDTO.setCus_Name("N/A");
            billDTO.setCus_AccountNumber("Customer Not Found");
        }
    }
}