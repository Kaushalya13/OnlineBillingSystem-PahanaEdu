package org.icbt.onlinebillingsystempahanaedu.item.entity;

import org.icbt.onlinebillingsystempahanaedu.core.repo.SuperEntity;

import java.sql.Timestamp;

/**
 * author : Niwanthi
 * date : 8/9/2025
 * time : 1:08 PM
 */
public class ItemEntity implements SuperEntity {
    private int id;
    private String itemName;
    private double unitPrice;
    private int quantity;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Timestamp getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Timestamp deletedAt) {
        this.deletedAt = deletedAt;
    }
}
