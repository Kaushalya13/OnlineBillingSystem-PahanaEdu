package org.icbt.onlinebillingsystempahanaedu.customer.dto;


import org.icbt.onlinebillingsystempahanaedu.core.repo.SuperDTO;

import java.sql.Timestamp;

public class CustomerDTO implements SuperDTO {
    private int cus_Id;
    private String cus_Name;
    private String cus_Address;
    private String cus_Mobile;
    private String cus_AccountNumber;
    private int units_consumed;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;

    public int getCus_Id() {
        return cus_Id;
    }

    public void setCus_Id(int cus_Id) {
        this.cus_Id = cus_Id;
    }

    public String getCus_Name() {
        return cus_Name;
    }

    public void setCus_Name(String cus_Name) {
        this.cus_Name = cus_Name;
    }

    public String getCus_Address() {
        return cus_Address;
    }

    public void setCus_Address(String cus_Address) {
        this.cus_Address = cus_Address;
    }

    public String getCus_Mobile() {
        return cus_Mobile;
    }

    public void setCus_Mobile(String cus_Mobile) {
        this.cus_Mobile = cus_Mobile;
    }

    public String getCus_AccountNumber() {
        return cus_AccountNumber;
    }

    public void setCus_AccountNumber(String cus_AccountNumber) {
        this.cus_AccountNumber = cus_AccountNumber;
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

    public int getUnits_consumed() {
        return units_consumed;
    }

    public void setUnits_consumed(int units_consumed) {
        this.units_consumed = units_consumed;
    }
}
