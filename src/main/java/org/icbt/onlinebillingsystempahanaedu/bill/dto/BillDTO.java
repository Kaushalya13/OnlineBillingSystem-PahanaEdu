package org.icbt.onlinebillingsystempahanaedu.bill.dto;

import org.icbt.onlinebillingsystempahanaedu.core.repo.SuperDTO;

import java.sql.Timestamp;
import java.util.List;

/**
     * author : Niwanthi
     * date : 8/16/2025
     * time : 2:36 PM
 */public class BillDTO implements SuperDTO {
    private int id;
    private int customer_id;
    private String cus_AccountNumber;
    private String cus_Name;
    private Double total_amount;
    private List<BillDetailsDTO> billDetails;

    private Timestamp created_at;
    private Timestamp deleted_at;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(int customer_id) {
        this.customer_id = customer_id;
    }

    public String getCus_AccountNumber() {
        return cus_AccountNumber;
    }

    public void setCus_AccountNumber(String cus_AccountNumber) {
        this.cus_AccountNumber = cus_AccountNumber;
    }

    public String getCus_Name() {
        return cus_Name;
    }

    public void setCus_Name(String cus_Name) {
        this.cus_Name = cus_Name;
    }

    public Double getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(Double total_amount) {
        this.total_amount = total_amount;
    }

    public List<BillDetailsDTO> getBillDetails() {
        return billDetails;
    }

    public void setBillDetails(List<BillDetailsDTO> billDetails) {
        this.billDetails = billDetails;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }

    public Timestamp getDeleted_at() {
        return deleted_at;
    }

    public void setDeleted_at(Timestamp deleted_at) {
        this.deleted_at = deleted_at;
    }
}
