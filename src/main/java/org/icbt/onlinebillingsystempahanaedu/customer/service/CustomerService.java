package org.icbt.onlinebillingsystempahanaedu.customer.service;

import org.icbt.onlinebillingsystempahanaedu.core.repo.CrudService;
import org.icbt.onlinebillingsystempahanaedu.customer.dto.CustomerDTO;

import java.sql.Connection;
import java.sql.SQLException;


/**
 * author : Niwanthi
 * date : 7/20/2025
 * time : 11:33 PM
 */
public interface CustomerService extends CrudService<CustomerDTO> {
    CustomerDTO findByAccountNumber(Object... args) throws SQLException, ClassNotFoundException;
    CustomerDTO findByMobile(Object... args) throws SQLException, ClassNotFoundException;
    int getTotalCustomers() throws SQLException, ClassNotFoundException;
}
