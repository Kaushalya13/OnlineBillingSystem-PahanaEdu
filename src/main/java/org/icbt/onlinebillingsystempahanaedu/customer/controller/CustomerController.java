package org.icbt.onlinebillingsystempahanaedu.customer.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.icbt.onlinebillingsystempahanaedu.core.exception.CustomException;
import org.icbt.onlinebillingsystempahanaedu.core.util.SendResponse;
import org.icbt.onlinebillingsystempahanaedu.customer.dto.CustomerDTO;
import org.icbt.onlinebillingsystempahanaedu.customer.service.CustomerService;
import org.icbt.onlinebillingsystempahanaedu.customer.service.impl.CustomerServiceImpl;
import org.icbt.onlinebillingsystempahanaedu.user.service.UserService;
import org.icbt.onlinebillingsystempahanaedu.user.service.impl.UserServiceImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@WebServlet(name = "CustomerController", urlPatterns = "/customers")
public class CustomerController extends HttpServlet {
    private CustomerService customerService;
    private UserService userService;
    private static final Logger logger = Logger.getLogger(CustomerController.class.getName());
    private static final int INITIAL_ADMIN_ID = 1;

    @Override
    public void init() {
        customerService = new CustomerServiceImpl();
        userService = new UserServiceImpl();
    }

    private Integer getUserIdFromSession(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("userId") != null) {
            return (Integer) session.getAttribute("userId");
        }
        return null;
    }

    private String getUserRoleFromSession(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("role") != null) {
            return (String) session.getAttribute("role");
        }
        return null;
    }

    private Map<String, Object> customerDTOtoMap(CustomerDTO customerDTO) {
        if (customerDTO == null) return null;

        Map<String, Object> map = new HashMap<>();
        map.put("cus_Id", customerDTO.getCus_Id());
        map.put("cus_Name", customerDTO.getCus_Name());
        map.put("cus_Address", customerDTO.getCus_Address());
        map.put("cus_Mobile", customerDTO.getCus_Mobile());
        map.put("cus_AccountNumber", customerDTO.getCus_AccountNumber());
        map.put("units_consumed", customerDTO.getUnits_consumed());
        map.put("createdAt", customerDTO.getCreatedAt());
        map.put("updatedAt", customerDTO.getUpdatedAt());
        map.put("deletedAt", customerDTO.getDeletedAt());

        return map;
    }

    private String safeDecode(String value) {
        try {
            return URLDecoder.decode(value, StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error decoding URL-encoded parameter: " + e.getMessage());
            return "";
        }
    }

    private Map<String, String> parseUrlEncodedBody(HttpServletRequest req) throws IOException {
        Map<String, String> params = new HashMap<>();
        try (BufferedReader reader = req.getReader()) {
            String body = reader.lines().collect(Collectors.joining(System.lineSeparator()));
            if (body != null && !body.isEmpty()) {
                Arrays.stream(body.split("&"))
                        .forEach(pair -> {
                            String[] keyValue = pair.split("=", 2);
                            if (keyValue.length == 2) {
                                String key = safeDecode(keyValue[0]);
                                String value = safeDecode(keyValue[1]);
                                params.put(key, value);
                            }
                        });
            }
        }
        return params;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String currentUserRole = getUserRoleFromSession(request);
        Integer currentUserId = getUserIdFromSession(request);

        if (!"ADMIN".equals(currentUserRole)) {
            SendResponse.sendJson(response, HttpServletResponse.SC_FORBIDDEN, Map.of("message", "Unauthorized: Only admins can add customers."));
            return;
        }

        if (currentUserId == null) {
            SendResponse.sendJson(response, HttpServletResponse.SC_UNAUTHORIZED, Map.of("message", "Unauthorized: Please log in."));
            return;
        }

        try {
            CustomerDTO dto = new CustomerDTO();
            dto.setCus_Name(request.getParameter("cus_Name"));
            dto.setCus_Address(request.getParameter("cus_Address"));
            dto.setCus_Mobile(request.getParameter("cus_Mobile"));
            dto.setCus_AccountNumber(request.getParameter("cus_AccountNumber"));
            String unitsConsumedStr = request.getParameter("units_consumed");
            dto.setUnits_consumed(unitsConsumedStr != null && !unitsConsumedStr.isEmpty() ? Integer.parseInt(unitsConsumedStr) : 0);

            if (dto.getCus_AccountNumber() == null || dto.getCus_AccountNumber().trim().isEmpty() ||
                    dto.getCus_Name() == null || dto.getCus_Name().trim().isEmpty() ||
                    dto.getCus_Mobile() == null || dto.getCus_Mobile().trim().isEmpty()) {
                SendResponse.sendJson(response, HttpServletResponse.SC_BAD_REQUEST, Map.of("message", "Missing required customer fields (accountNumber, name, phone)."));
                return;
            }

            boolean isAdded = customerService.add(dto);
            if (isAdded) {
                CustomerDTO addedCustomer = customerService.findByAccountNumber(dto.getCus_AccountNumber());
                SendResponse.sendJson(response, HttpServletResponse.SC_CREATED, Map.of("message", "Customer added successfully.", "customer", customerDTOtoMap(addedCustomer)));
            } else {
                SendResponse.sendJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Map.of("message", "Customer addition failed unexpectedly."));
            }
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING, "Error Invalid number format for unit consumed: " + e.getMessage());
            SendResponse.sendJson(response, HttpServletResponse.SC_BAD_REQUEST, Map.of("message", "Invalid unit consumed. Enter valid number"));
        } catch (CustomException e) {
            logger.log(Level.WARNING, "Error Custom Exception: " + e.getMessage() + " - " + e.getExceptionType().name());
            String error;
            int statusCode = HttpServletResponse.SC_BAD_REQUEST;
            switch (e.getExceptionType()) {
                case CUSTOMER_ACCOUNT_NUMBER_ALREADY_EXISTS:
                    error = "Customer account number already exists.";
                    break;
                case CUSTOMER_PHONE_NUMBER_ALREADY_EXISTS:
                    error = "Customer phone number already exists.";
                    break;
                case CUSTOMER_CREATION_FAILED:
                    error = "Failed to create customer due to a system error.";
                    statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                    break;
                case DATABASE_ERROR:
                    error = "A database error occurred during customer addition.";
                    statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                    break;
                default:
                    error = "An unexpected business error occurred: " + e.getExceptionType().name();
                    break;
            }
            SendResponse.sendJson(response, statusCode, Map.of("message", error));
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error add customer : " + e.getMessage());
            SendResponse.sendJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Map.of("message", "Internal server error."));
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String idStr = request.getParameter("id");
            if (idStr != null && !idStr.isEmpty()) {
                int id = Integer.parseInt(idStr);
                CustomerDTO dto = customerService.searchById(id);
                if (dto != null) {
                    SendResponse.sendJson(response, HttpServletResponse.SC_OK, Map.of("message", "Customer found successfully.", "customer", customerDTOtoMap(dto)));
                } else {
                    SendResponse.sendJson(response, HttpServletResponse.SC_NOT_FOUND, Map.of("message", "Customer not found."));
                }
                return;
            }

            String accountNumber = request.getParameter("cus_AccountNumber");
            if (accountNumber != null && !accountNumber.isEmpty()) {
                CustomerDTO dto = customerService.findByAccountNumber(accountNumber);
                if (dto != null) {
                    SendResponse.sendJson(response, HttpServletResponse.SC_OK, Map.of("message", "Customer found successfully.", "customer", customerDTOtoMap(dto)));
                } else {
                    SendResponse.sendJson(response, HttpServletResponse.SC_NOT_FOUND, Map.of("message", "Customer not found."));
                }
            } else {
                String searchTerm = request.getParameter("search");
                Map<String, String> searchParams = new HashMap<>();
                if (searchTerm != null && !searchTerm.isEmpty()) {
                    searchParams.put("search", searchTerm);
                }

                List<CustomerDTO> list = customerService.getAll(searchParams);
                List<Map<String, Object>> customerMaps = list.stream()
                        .map(this::customerDTOtoMap)
                        .collect(Collectors.toList());
                SendResponse.sendJson(response, HttpServletResponse.SC_OK, customerMaps);
            }
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING, "Error Invalid number format for ID or units consumed: " + e.getMessage());
            SendResponse.sendJson(response, HttpServletResponse.SC_BAD_REQUEST, Map.of("message", "Invalid number format in request."));
        } catch (CustomException e) {
            logger.log(Level.WARNING, "Error Custom Exception: " + e.getMessage() + " - " + e.getExceptionType().name());
            String error;
            int statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            if (e.getExceptionType() == CustomException.ExceptionType.CUSTOMER_NOT_FOUND) {
                error = "Customer not found.";
                statusCode = HttpServletResponse.SC_NOT_FOUND;
            } else {
                error = "An unexpected business error occurred: " + e.getExceptionType().name();
            }
            SendResponse.sendJson(response, statusCode, Map.of("message", error));
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error add customer : " + e.getMessage());
            SendResponse.sendJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Map.of("message", "Internal server error."));
        }
    }
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        String currentUserRole = getUserRoleFromSession(request);
        Integer currentUserId = getUserIdFromSession(request);

        if (!"ADMIN".equals(currentUserRole)) {
            SendResponse.sendJson(response, HttpServletResponse.SC_FORBIDDEN, Map.of("message", "Unauthorized: Only admins can update items."));
            return;
        }
        if (currentUserId == null) {
            SendResponse.sendJson(response, HttpServletResponse.SC_UNAUTHORIZED, Map.of("message", "Unauthorized: Please log in."));
            return;
        }

        try {
            Map<String, String> params = parseUrlEncodedBody(request);
            String idStr = params.get("cus_Id");

            CustomerDTO dto = new CustomerDTO();

            if (idStr != null && !idStr.isEmpty()) {
                dto.setCus_Id(Integer.parseInt(idStr));
            }else {
                SendResponse.sendJson(response, HttpServletResponse.SC_BAD_REQUEST, Map.of("message", "Invalid customer id"));
                return;
            }

            dto.setCus_Name(params.get("cus_Name"));
            dto.setCus_Address(params.get("cus_Address"));
            dto.setCus_Mobile(params.get("cus_Mobile"));
            dto.setCus_AccountNumber(params.get("cus_AccountNumber"));
            String unitsConsumedStr = params.get("units_consumed");
            dto.setUnits_consumed(unitsConsumedStr != null && !unitsConsumedStr.isEmpty() ? Integer.parseInt(unitsConsumedStr) : 0);

            if (dto.getCus_AccountNumber() == null || dto.getCus_AccountNumber().trim().isEmpty() ||
                    dto.getCus_Name() == null || dto.getCus_Name().trim().isEmpty() ||
                    dto.getCus_Mobile() == null || dto.getCus_Mobile().trim().isEmpty()) {
                SendResponse.sendJson(response, HttpServletResponse.SC_BAD_REQUEST, Map.of("message", "Missing required customer fields for update"));
                return;
            }

            boolean updated = customerService.update(dto);

            if (updated) {
                CustomerDTO updateCustomer = customerService.findByAccountNumber(dto.getCus_AccountNumber());
                SendResponse.sendJson(response, HttpServletResponse.SC_OK,Map.of(
                        "message", "Customer updated successfully.",
                        "customer", customerDTOtoMap(updateCustomer)
                ));
            }else {
                SendResponse.sendJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Map.of("message", "Customer update failed."));
            }
        }catch (NumberFormatException e){
            logger.log(Level.WARNING, "Error Invalid number format for unit consumed: " + e.getMessage());
            SendResponse.sendJson(response,HttpServletResponse.SC_BAD_REQUEST, Map.of("message", "Invalid unit consumed. Enter valid number"));
        }catch (CustomException e){
            logger.log(Level.WARNING, "Error Custom Exception: " + e.getMessage() + " - " + e.getExceptionType().name());
            String error;
            int statusCode = HttpServletResponse.SC_BAD_REQUEST;
            switch (e.getExceptionType()){
                case CUSTOMER_NOT_FOUND:
                    error = "Customer not found.";
                    statusCode = HttpServletResponse.SC_NOT_FOUND;
                    break;
                case CUSTOMER_PHONE_NUMBER_ALREADY_EXISTS:
                    error = "Another customer phone number already exists.";
                    break;
                case CUSTOMER_UPDATE_FAILED:
                    error = "Failed to update customer system error.";
                    statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                    break;
                case DATABASE_ERROR:
                    error = "A database error.";
                    statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                    break;
                default:
                    error = "An unexpected business error occurred: " + e.getExceptionType().name();
                    break;
            }
            SendResponse.sendJson(response, statusCode, Map.of("message", error));
        }catch (Exception e) {
            logger.log(Level.WARNING, "Error add customer : " + e.getMessage());
            SendResponse.sendJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Map.of("message", "Internal server error."));
        }

    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        String currentUserRole = getUserRoleFromSession(request);
        Integer currentUserId = getUserIdFromSession(request);

        if (!"ADMIN".equals(currentUserRole) || (currentUserId == null || !currentUserId.equals(INITIAL_ADMIN_ID))) {
            SendResponse.sendJson(response, HttpServletResponse.SC_FORBIDDEN,
                    Map.of("message", "Unauthorized: Only the Initial Admin can delete items."));
            return;
        }

        String accountNumber = request.getParameter("accountNumber");
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            SendResponse.sendJson(response, HttpServletResponse.SC_BAD_REQUEST, Map.of("message", "Invalid account number"));
            return;
        }

        try {
            boolean deleted = customerService.delete(currentUserId,accountNumber);
            if (deleted) {
                SendResponse.sendJson(response, HttpServletResponse.SC_OK, Map.of("message", "Customer deleted successfully."));
            }else {
                SendResponse.sendJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Map.of("message", "Customer delete failed."));
            }
        }catch (CustomException e){
            logger.log(Level.WARNING, "Error Custom Exception: " + e.getMessage() + " - " + e.getExceptionType().name());
            String error;
            int statusCode = HttpServletResponse.SC_BAD_REQUEST;
            switch (e.getExceptionType()){
                case CUSTOMER_NOT_FOUND:
                    error = "Customer not found.";
                    statusCode = HttpServletResponse.SC_NOT_FOUND;
                    break;
                case CUSTOMER_DELETION_FAILED:
                    error = "Failed to delete customer.";
                    statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                    break;
                case DATABASE_ERROR:
                    error = "A database error.";
                    statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                    break;
                default:
                    error = "An unexpected business error occurred: " + e.getExceptionType().name();
                    break;
            }
            SendResponse.sendJson(response, statusCode, Map.of("message", error));
        }catch (Exception e) {
            logger.log(Level.WARNING, "Error deleting customer : " + e.getMessage());
            SendResponse.sendJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Map.of("message", "Internal server error."));
        }

    }
}