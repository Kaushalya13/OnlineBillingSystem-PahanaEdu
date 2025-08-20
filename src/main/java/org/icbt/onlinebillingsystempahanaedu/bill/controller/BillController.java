package org.icbt.onlinebillingsystempahanaedu.bill.controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.icbt.onlinebillingsystempahanaedu.bill.dto.BillDTO;
import org.icbt.onlinebillingsystempahanaedu.bill.dto.BillDetailsDTO;
import org.icbt.onlinebillingsystempahanaedu.bill.mapper.BillMapper;
import org.icbt.onlinebillingsystempahanaedu.bill.sevice.BillService;
import org.icbt.onlinebillingsystempahanaedu.bill.sevice.impl.BillServiceImpl;
import org.icbt.onlinebillingsystempahanaedu.core.exception.CustomException;
import org.icbt.onlinebillingsystempahanaedu.core.util.SendResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * author : Niwanthi
 * date : 8/16/2025
 * time : 2:33 PM
 */
@WebServlet(name = "BillController", urlPatterns = "/bills")
public class BillController extends HttpServlet {
    private BillService billService;
    private static final Logger logger = Logger.getLogger(BillController.class.getName());
    private static final int INITIAL_ADMIN_ID = 1;

    @Override
    public void init() {
        billService = new BillServiceImpl();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            HttpSession session = request.getSession(false);
            Integer currentUserId = (session != null) ? (Integer) session.getAttribute("userId") : null;

            if (currentUserId == null) {
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: Please log in.");
                return;
            }

            BillDTO billToCreate = extractBillData(request);
            BillDTO createdBill = billService.createBill(billToCreate, currentUserId);

            sendJsonResponse(response, HttpServletResponse.SC_CREATED, createdBill);

        } catch (CustomException e) {
            logger.log(Level.WARNING, "Business logic error creating bill: " + e.getExceptionType().name(), e);
            handleCustomException(response, e);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected server error during bill creation", e);
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An internal server error occurred.");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String billIdStr = request.getParameter("id");
            if (billIdStr != null && !billIdStr.trim().isEmpty()) {

                Integer billId = Integer.parseInt(billIdStr);
                BillDTO bill = billService.findBillById(billId);
                System.out.println("fetched bill" + bill);
                SendResponse.sendJson(response, HttpServletResponse.SC_OK, BillMapper.toMap(bill));

            } else {
                System.out.println("fetched all bills");
                Map<String, String> searchParams = new HashMap<>();
                String searchTerm = request.getParameter("search");
                if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                    searchParams.put("search", searchTerm);
                }
                List<BillDTO> bills = billService.findAllBills(searchParams);
                List<Map<String, Object>> billMaps = bills.stream()
                        .map(BillMapper::toMap)
                        .collect(Collectors.toList());
                SendResponse.sendJson(response, HttpServletResponse.SC_OK, billMaps);
            }
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING, "Invalid bill ID format provided", e);
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid ID format. Please provide a numeric ID.");
        } catch (CustomException e) {
            logger.log(Level.WARNING, "Business logic error fetching bills: " + e.getExceptionType().name(), e);
            handleCustomException(response, e);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected server error during bill retrieval", e);
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An internal server error occurred.");
        }
    }

    private BillDTO extractBillData(HttpServletRequest request) {
        logger.info("Received customerId: " + request.getParameter("customerId"));

        try {
            BillDTO bill = new BillDTO();
            String customerIdParam = request.getParameter("customerId");
            if (customerIdParam == null || customerIdParam.trim().isEmpty()) {
                throw new CustomException(CustomException.ExceptionType.INVALID_BILL_INPUTS);
            }
            bill.setCustomer_id(Integer.valueOf(customerIdParam));

            String[] itemArray = request.getParameterValues("item_id");
            String[] unitArray = request.getParameterValues("units");

            if (itemArray == null || unitArray == null || itemArray.length == 0 || itemArray.length != unitArray.length) {
                throw new CustomException(CustomException.ExceptionType.INVALID_BILL_INPUTS);
            }

            List<BillDetailsDTO> detailList = IntStream.range(0, itemArray.length)
                    .mapToObj(i -> {
                        BillDetailsDTO d = new BillDetailsDTO();
                        d.setItem_id(Integer.parseInt(itemArray[i]));
                        d.setUnits(Integer.parseInt(unitArray[i]));
                        return d;
                    })
                    .toList();

            bill.setBillDetails(detailList);
            return bill;

        } catch (NumberFormatException e) {
            logger.log(Level.WARNING, "Invalid number format in bill inputs", e);
            throw new CustomException(CustomException.ExceptionType.INVALID_BILL_INPUTS);
        }
    }

    private void handleCustomException(HttpServletResponse response, CustomException e) throws IOException {
        int statusCode;
        String message = e.getMessage() != null ? e.getMessage() : e.getExceptionType().name();

        switch (e.getExceptionType()) {
            case CUSTOMER_NOT_FOUND:
            case ITEM_NOT_FOUND:
                statusCode = HttpServletResponse.SC_NOT_FOUND;
                break;
            case INVALID_BILL_INPUTS:
            case INSUFFICIENT_STOCK:
                statusCode = HttpServletResponse.SC_BAD_REQUEST;
                break;
            case CUSTOMER_UPDATE_FAILED:
            case DATABASE_ERROR:
            default:
                statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                break;
        }
        sendErrorResponse(response, statusCode, message);
    }

    private void sendErrorResponse(HttpServletResponse response, int statusCode, String message) throws IOException {
        sendJsonResponse(response, statusCode, Map.of("message", message));
    }

    private void sendJsonResponse(HttpServletResponse response, int statusCode, Object data) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(statusCode);

        String jsonResponse;
        if (data instanceof BillDTO) {
            jsonResponse = billToJson((BillDTO) data);
        } else if (data instanceof List) {
            jsonResponse = billsListToJson((List<BillDTO>) data);
        } else if (data instanceof Map) {
            jsonResponse = mapToJson((Map<String, String>) data);
        } else {
            jsonResponse = "{\"error\":\"Unsupported data type for JSON conversion.\"}";
        }

        response.getWriter().write(jsonResponse);
    }

    private String escapeJson(String value) {
        if (value == null) {
            return "null";
        }
        return "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }

    private String mapToJson(Map<String, String> map) {
        return "{" + map.entrySet().stream()
                .map(entry -> escapeJson(entry.getKey()) + ":" + escapeJson(entry.getValue()))
                .collect(Collectors.joining(",")) + "}";
    }

    private String billDetailsToJson(BillDetailsDTO detail) {
        if (detail == null) return "null";
        return "{" +
                "\"id\":" + detail.getId() + "," +
                "\"bill_id\":" + detail.getBill_id() + "," +
                "\"item_id\":" + detail.getItem_id() + "," +
                "\"itemNameAtSale\":" + escapeJson(detail.getItem_name_at_sale()) + "," +
                "\"unitPriceAtSale\":" + detail.getUnit_price_at_sale() + "," +
                "\"units\":" + detail.getUnits() + "," +
                "\"total\":" + detail.getTotal() +
                "}";
    }

    private String billToJson(BillDTO bill) {
        if (bill == null) return "null";

        String detailsJson = "[]";
        if (bill.getBillDetails() != null && !bill.getBillDetails().isEmpty()) {
            detailsJson = "[" + bill.getBillDetails().stream()
                    .map(this::billDetailsToJson)
                    .collect(Collectors.joining(",")) + "]";
        }

        return "{" +
                "\"id\":" + bill.getId() + "," +
                "\"customer_id\":" + bill.getCustomer_id() + "," +
                "\"total_amount\":" + bill.getTotal_amount() + "," +
                "\"cus_Name\":" + escapeJson(bill.getCus_Name()) + "," +
                "\"cus_AccountNumber\":" + escapeJson(bill.getCus_AccountNumber()) + "," +
                "\"createdAt\":" + (bill.getCreated_at() != null ? "\"" + bill.getCreated_at().toString() + "\"" : "null") + "," +
                "\"billDetails\":" + detailsJson +
                "}";
    }

    private String billsListToJson(List<BillDTO> bills) {
        if (bills == null || bills.isEmpty()) return "[]";
        return "[" + bills.stream()
                .map(this::billToJson)
                .collect(Collectors.joining(",")) + "]";
    }
}