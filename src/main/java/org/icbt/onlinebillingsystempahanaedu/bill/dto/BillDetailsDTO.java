package org.icbt.onlinebillingsystempahanaedu.bill.dto;

import org.icbt.onlinebillingsystempahanaedu.core.repo.SuperDTO;

import java.sql.Timestamp;

/**
     * author : Niwanthi
     * date : 8/16/2025
     * time : 2:36 PM
 */
public class BillDetailsDTO implements SuperDTO {
    private int id;
    private int bill_id;
    private int item_id;
    private String item_name_at_sale;
    private Double unit_price_at_sale;
    private int units;
    private Double total;

    private Timestamp created_at;
    private Timestamp deleted_at;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBill_id() {
        return bill_id;
    }

    public void setBill_id(int bill_id) {
        this.bill_id = bill_id;
    }

    public int getItem_id() {
        return item_id;
    }

    public void setItem_id(int item_id) {
        this.item_id = item_id;
    }

    public String getItem_name_at_sale() {
        return item_name_at_sale;
    }

    public void setItem_name_at_sale(String item_name_at_sale) {
        this.item_name_at_sale = item_name_at_sale;
    }

    public Double getUnit_price_at_sale() {
        return unit_price_at_sale;
    }

    public void setUnit_price_at_sale(Double unit_price_at_sale) {
        this.unit_price_at_sale = unit_price_at_sale;
    }

    public int getUnits() {
        return units;
    }

    public void setUnits(int units) {
        this.units = units;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
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
