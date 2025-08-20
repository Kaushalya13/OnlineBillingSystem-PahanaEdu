package org.icbt.onlinebillingsystempahanaedu.dashboard.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.icbt.onlinebillingsystempahanaedu.bill.sevice.BillService;
import org.icbt.onlinebillingsystempahanaedu.bill.sevice.impl.BillServiceImpl;
import org.icbt.onlinebillingsystempahanaedu.customer.service.CustomerService;
import org.icbt.onlinebillingsystempahanaedu.customer.service.impl.CustomerServiceImpl;
import org.icbt.onlinebillingsystempahanaedu.item.dto.ItemDTO;
import org.icbt.onlinebillingsystempahanaedu.item.service.ItemService;
import org.icbt.onlinebillingsystempahanaedu.item.service.impl.ItemServiceImpl;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "DashboardDataServlet", urlPatterns = "/dashboard/data")
public class DashboardController extends HttpServlet {

    private static final Logger logger = Logger.getLogger(DashboardController.class.getName());

    private CustomerService customerService;
    private ItemService itemService;
    private BillService billService;

    @Override
    public void init() {
        customerService = new CustomerServiceImpl();
        itemService = new ItemServiceImpl();
        billService = new BillServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            int totalCustomers = customerService.getTotalCustomers();
            int itemStock = itemService.getItemsCount();
            int placedOrders = billService.getTotalBills();

            List<ItemDTO> items = itemService.getAll(new HashMap<>());

            StringBuilder itemsJson = new StringBuilder("[");
            for (int i = 0; i < items.size(); i++) {
                ItemDTO item = items.get(i);
                String itemName = escapeJson(item.getItemName());

                itemsJson.append("{\"itemName\":\"").append(itemName)
                        .append("\",\"quantity\":").append(item.getQuantity()).append("}");

                if (i < items.size() - 1) {
                    itemsJson.append(",");
                }
            }
            itemsJson.append("]");

            String jsonResponse = "{"
                    + "\"totalCustomers\":" + totalCustomers + ","
                    + "\"itemStock\":" + itemStock + ","
                    + "\"placedOrders\":" + placedOrders + ","
                    + "\"items\":" + itemsJson.toString()
                    + "}";

            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            PrintWriter out = resp.getWriter();
            out.write(jsonResponse);
            out.flush();

        } catch (SQLException | ClassNotFoundException e) {
            logger.log(Level.SEVERE, "Failed to load dashboard data", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to load dashboard data");
        }
    }

    private static String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
