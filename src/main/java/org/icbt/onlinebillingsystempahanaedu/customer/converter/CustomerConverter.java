package org.icbt.onlinebillingsystempahanaedu.customer.converter;

import org.icbt.onlinebillingsystempahanaedu.customer.dto.CustomerDTO;
import org.icbt.onlinebillingsystempahanaedu.customer.entity.CustomerEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * author : Niwanthi
 * date : 7/20/2025
 * time : 11:32 PM
 */
public class CustomerConverter {
    //convert DTO to Entity
    public static CustomerEntity convertCustomerToCustomerEntity(CustomerDTO customerDTO) {
        if (customerDTO == null) {
            return null;
        }
        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setCus_Id(customerDTO.getCus_Id());
        customerEntity.setCus_Name(customerDTO.getCus_Name());
        customerEntity.setCus_Address(customerDTO.getCus_Address());
        customerEntity.setCus_Mobile(customerDTO.getCus_Mobile());
        customerEntity.setCus_Email(customerDTO.getCus_Email());
        customerEntity.setCus_AccountNumber(customerDTO.getCus_AccountNumber());
        customerEntity.setUnits_consumed(customerDTO.getUnits_consumed());
        customerEntity.setCreatedAt(customerDTO.getCreatedAt());
        customerEntity.setUpdatedAt(customerDTO.getUpdatedAt());
        customerEntity.setDeletedAt(customerDTO.getDeletedAt());

        return customerEntity;
    }

    //convert Entity to DTO
    public static CustomerDTO convertCustomerEntityToCustomerDTO(CustomerEntity customerEntity) {
        if (customerEntity == null) {
            return null;
        }
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setCus_Id(customerEntity.getCus_Id());
        customerDTO.setCus_Name(customerEntity.getCus_Name());
        customerDTO.setCus_Address(customerEntity.getCus_Address());
        customerDTO.setCus_Mobile(customerEntity.getCus_Mobile());
        customerDTO.setCus_Email(customerEntity.getCus_Email());
        customerDTO.setCus_AccountNumber(customerEntity.getCus_AccountNumber());
        customerDTO.setUnits_consumed(customerEntity.getUnits_consumed());
        customerDTO.setCreatedAt(customerEntity.getCreatedAt());
        customerDTO.setUpdatedAt(customerEntity.getUpdatedAt());
        customerDTO.setDeletedAt(customerEntity.getDeletedAt());

        return customerDTO;

    }

    public static List<CustomerDTO> convertCustomerEntityListToCustomerDTOList(List<CustomerEntity> customerEntityList) {
        List<CustomerDTO> customerDTOList = new ArrayList<>();
        if (customerEntityList != null) {
            for (CustomerEntity customerEntity : customerEntityList) {
                customerDTOList.add(convertCustomerEntityToCustomerDTO(customerEntity));
            }
        }
        return customerDTOList;
    }
}
