package org.icbt.onlinebillingsystempahanaedu.bill.sevice;


import org.icbt.onlinebillingsystempahanaedu.bill.dto.BillDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * author : Niwanthi
 * date : 8/16/2025
 * time : 2:38 PM
 */
public interface BillService {
    BillDTO createBill(BillDTO billDTO, Integer generatedByUserId) throws ClassNotFoundException;

    boolean deleteBill(Integer deletedByUserId, Integer billId) throws ClassNotFoundException;

    BillDTO findBillById(Integer billId) throws ClassNotFoundException;

    List<BillDTO> findAllBills(Map<String, String> searchParams) throws ClassNotFoundException;

    int getTotalBills() throws SQLException, ClassNotFoundException;

}
