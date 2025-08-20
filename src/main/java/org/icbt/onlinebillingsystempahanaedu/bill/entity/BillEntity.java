package org.icbt.onlinebillingsystempahanaedu.bill.entity;

import org.icbt.onlinebillingsystempahanaedu.core.repo.SuperEntity;

import java.sql.Timestamp;
import java.util.List;

/**
     * author : Niwanthi
     * date : 8/16/2025
     * time : 2:36 PM
 */
public class BillEntity implements SuperEntity {
    private int id;
    private int customer_id;
    private Double total_amount;
    private List<BillDetailsEntity> billDetails;

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

    public Double getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(Double total_amount) {
        this.total_amount = total_amount;
    }

    public List<BillDetailsEntity> getBillDetails() {
        return billDetails;
    }

    public void setBillDetails(List<BillDetailsEntity> billDetails) {
        this.billDetails = billDetails;
    }

    public Timestamp getDeleted_at() {
        return deleted_at;
    }

    public void setDeleted_at(Timestamp deleted_at) {
        this.deleted_at = deleted_at;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }

}
