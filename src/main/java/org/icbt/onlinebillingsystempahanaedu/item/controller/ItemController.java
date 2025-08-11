package org.icbt.onlinebillingsystempahanaedu.item.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.icbt.onlinebillingsystempahanaedu.core.exception.CustomException;
import org.icbt.onlinebillingsystempahanaedu.core.util.SendResponse;
import org.icbt.onlinebillingsystempahanaedu.item.dto.ItemDTO;
import org.icbt.onlinebillingsystempahanaedu.item.service.ItemService;
import org.icbt.onlinebillingsystempahanaedu.item.service.impl.ItemServiceImpl;
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

/**
 * author : Niwanthi
 * date : 8/9/2025
 * time : 1:12 PM
 */
@WebServlet(name = "ItemController", urlPatterns = "/items")
public class ItemController extends HttpServlet {

    private ItemService itemService;
    private UserService userService;
    private static final Logger logger = Logger.getLogger(ItemController.class.getName());
    private static final int INITIAL_ADMIN_ID = 1;

    @Override
    public void init() {
        itemService = new ItemServiceImpl();
        userService = new UserServiceImpl();
    }

    // Helper to get userId from session
    private Integer getUserIdFromSession(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("userId") != null) {
            return (Integer) session.getAttribute("userId");
        }
        return null;
    }

    // Helper to get user role from session
    private String getUserRoleFromSession(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("role") != null) {
            return (String) session.getAttribute("role");
        }
        return null;
    }

    // Main method using the helper to avoid duplication
    private Map<String, Object> itemDtoToMap(ItemDTO dto, String createdByUsername, String updatedByUsername, String deletedByUsername) {
        if (dto == null) return null;

        Map<String, Object> map = new HashMap<>();
        map.put("id", dto.getId());
        map.put("itemName", dto.getItemName());
        map.put("unitPrice", dto.getUnitPrice());
        map.put("quantity", dto.getQuantity());
        map.put("createdAt", dto.getCreatedAt());
        map.put("updatedAt", dto.getUpdatedAt());
        map.put("deletedAt", dto.getDeletedAt());

        return map;
    }


    // Helper to safely decode URL-encoded strings
    private String safeDecode(String value) {
        try {
            return URLDecoder.decode(value, StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error decoding URL-encoded parameter: " + e.getMessage());
            return "";
        }
    }

    // Helper to parse x-www-form-urlencoded body for POST/PUT
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
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String currentUserRole = getUserRoleFromSession(req);
        Integer currentUserId = getUserIdFromSession(req);

        if (!"ADMIN".equals(currentUserRole)) {
            SendResponse.sendJson(resp, HttpServletResponse.SC_FORBIDDEN, Map.of("message", "Unauthorized: Only admins can add items."));
            return;
        }
        if (currentUserId == null) {
            SendResponse.sendJson(resp, HttpServletResponse.SC_UNAUTHORIZED, Map.of("message", "Unauthorized: Please log in."));
            return;
        }

        try {
            ItemDTO dto = new ItemDTO();
            dto.setItemName(req.getParameter("itemName"));
            String unitPriceStr = req.getParameter("unitPrice");
            String quantityStr = req.getParameter("quantity");

            // Basic validation
            if (dto.getItemName() == null || dto.getItemName().trim().isEmpty() ||
                    unitPriceStr == null || unitPriceStr.trim().isEmpty() ||
                    quantityStr == null || quantityStr.trim().isEmpty()) {
                SendResponse.sendJson(resp, HttpServletResponse.SC_BAD_REQUEST, Map.of("message", "Missing required item fields (itemName, unitPrice, quantity)."));
                return;
            }

            dto.setUnitPrice(Double.parseDouble(unitPriceStr));
            dto.setQuantity(Integer.parseInt(quantityStr));

            boolean isAdded = itemService.add(dto);
            if (isAdded) {
                ItemDTO addedItem = itemService.findByName(dto.getItemName());
                SendResponse.sendJson(resp, HttpServletResponse.SC_CREATED, Map.of("message", "Item added successfully", "item", itemDtoToMap(addedItem)));
            } else {
                SendResponse.sendJson(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Map.of("message", "Item addition failed unexpectedly."));
            }
        } catch (NumberFormatException e) {
            logger.log(Level.SEVERE, "Error during item addition: " + e.getMessage(), e);
            SendResponse.sendJson(resp, HttpServletResponse.SC_BAD_REQUEST, Map.of("message", "Invalid number format for price or quantity."));
        }catch (CustomException e){
            logger.log(Level.SEVERE, "Error during item addition: " + e.getMessage(), e);
            String errorMessage;
            int statusCode = HttpServletResponse.SC_BAD_REQUEST;

            switch (e.getExceptionType()) {
                case ITEM_ALREADY_EXISTS:
                    errorMessage = "Item with this name already exists.";
                    break;
                case ITEM_CREATION_FAILED:
                    errorMessage = "Failed to create item due to a system error.";
                    statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                    break;
                case DATABASE_ERROR:
                    errorMessage = "A database error occurred during item addition.";
                    statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                    break;
                default:
                    errorMessage = "An unexpected business error occurred: " + e.getExceptionType().name();
                    break;
            }
            SendResponse.sendJson(resp, statusCode, Map.of("message", errorMessage));

        }catch (Exception e){
            logger.log(Level.SEVERE, "Error during item addition: " + e.getMessage(), e);
            SendResponse.sendJson(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Map.of("message", e.getMessage()));
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try {
            String idStr = req.getParameter("id");
            String name = req.getParameter("name");

            if (idStr != null && !idStr.trim().isEmpty()) {
                Integer itemId = Integer.parseInt(idStr);
                ItemDTO dto = itemService.searchById(itemId);
                if (dto != null) {
                    SendResponse.sendJson(resp, HttpServletResponse.SC_OK, itemDtoToMap(dto, null, null, null));
                } else {
                    SendResponse.sendJson(resp, HttpServletResponse.SC_NOT_FOUND, Map.of("message", "Item not found."));
                }
            } else if (name != null && !name.trim().isEmpty()) {
                ItemDTO dto = itemService.findByName(name);
                if (dto != null) {
                    SendResponse.sendJson(resp, HttpServletResponse.SC_OK, itemDtoToMap(dto));
                } else {
                    SendResponse.sendJson(resp, HttpServletResponse.SC_NOT_FOUND, Map.of("message", "Item not found."));
                }
            } else {
                Map<String, String> searchParams = new HashMap<>();
                String searchTerm = req.getParameter("search");
                if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                    searchParams.put("search", searchTerm);
                }

                List<ItemDTO> list = itemService.getAll(searchParams);
                List<Map<String, Object>> itemMaps = list.stream()
                        .map(this::itemDtoToMap)
                        .collect(Collectors.toList());

                SendResponse.sendJson(resp, HttpServletResponse.SC_OK, itemMaps);
            }
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING, "Invalid ID format for GET /items: " + e.getMessage());
            SendResponse.sendJson(resp, HttpServletResponse.SC_BAD_REQUEST, Map.of("message", "Invalid item ID format."));
        } catch (CustomException e) {
            logger.log(Level.SEVERE, "Error during GET /items: " + e.getMessage(), e);
            String errorMessage;
            int statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            if (e.getExceptionType() == CustomException.ExceptionType.ITEM_NOT_FOUND) {
                errorMessage = "Item not found.";
                statusCode = HttpServletResponse.SC_NOT_FOUND;
            } else {
                errorMessage = "An unexpected error occurred.";
            }
            SendResponse.sendJson(resp, statusCode, Map.of("message", errorMessage));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error during GET /items: " + e.getMessage(), e);
            SendResponse.sendJson(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Map.of("message", "Internal server error."));
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String currentUserRole = getUserRoleFromSession(req);
        Integer currentUserId = getUserIdFromSession(req);

        if (!"ADMIN".equals(currentUserRole)) {
            SendResponse.sendJson(resp, HttpServletResponse.SC_FORBIDDEN, Map.of("message", "Unauthorized: Only admins can update items."));
            return;
        }
        if (currentUserId == null) {
            SendResponse.sendJson(resp, HttpServletResponse.SC_UNAUTHORIZED, Map.of("message", "Unauthorized: Please log in."));
            return;
        }

        try {
            Map<String, String> params = parseUrlEncodedBody(req);
            String action = params.get("action");
            String idStr = params.get("id");

            if (idStr == null || idStr.isEmpty()) {
                SendResponse.sendJson(resp, HttpServletResponse.SC_BAD_REQUEST, Map.of("message", "Item ID is required for this action."));
                return;
            }

            Integer itemId = Integer.parseInt(idStr);

            if ("restock".equals(action)) {
                // Restock action
                String qtyToAddStr = params.get("quantityToAdd");
                if (qtyToAddStr == null || qtyToAddStr.isEmpty()) {
                    SendResponse.sendJson(resp, HttpServletResponse.SC_BAD_REQUEST, Map.of("message", "Quantity to add is required for restock."));
                    return;
                }
                int qtyToAdd = Integer.parseInt(qtyToAddStr);

                boolean restocked = itemService.restockItem(itemId, qtyToAdd);
                if (restocked) {
                    ItemDTO updatedItem = itemService.searchById(itemId);
                    SendResponse.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                            "message", "Item restocked successfully",
                            "item", itemDtoToMap(updatedItem)
                    ));
                } else {
                    SendResponse.sendJson(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Map.of("message", "Failed to restock item."));
                }

            } else if ("update".equalsIgnoreCase(action)) {
                // Update action
                ItemDTO dto = new ItemDTO();
                dto.setId(itemId);
                dto.setItemName(params.get("itemName"));
                String unitPriceStr = params.get("unitPrice");
                String quantityStr = params.get("quantity");

                if (dto.getItemName() == null || dto.getItemName().trim().isEmpty() ||
                        unitPriceStr == null || unitPriceStr.trim().isEmpty() ||
                        quantityStr == null || quantityStr.trim().isEmpty()) {
                    SendResponse.sendJson(resp, HttpServletResponse.SC_BAD_REQUEST, Map.of("message", "Missing required fields for update."));
                    return;
                }

                dto.setUnitPrice(Double.parseDouble(unitPriceStr));
                dto.setQuantity(Integer.parseInt(quantityStr));

                boolean updated = itemService.update(dto);
                if (updated) {
                    ItemDTO updatedItem = itemService.searchById(dto.getId());
                    SendResponse.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                            "message", "Item updated successfully",
                            "item", itemDtoToMap(updatedItem)
                    ));
                } else {
                    SendResponse.sendJson(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Map.of("message", "Failed to update item."));
                }

            } else {
                SendResponse.sendJson(resp, HttpServletResponse.SC_BAD_REQUEST, Map.of("message", "Invalid action."));
            }

        } catch (NumberFormatException e) {
            logger.log(Level.WARNING, "Invalid number format for price, quantity, or ID: " + e.getMessage());
            SendResponse.sendJson(resp, HttpServletResponse.SC_BAD_REQUEST, Map.of("message", "Invalid number format in request."));
        } catch (CustomException e) {
            logger.log(Level.SEVERE, "Error in PUT /items: " + e.getMessage(), e);
            String errorMessage;
            int statusCode = HttpServletResponse.SC_BAD_REQUEST;
            switch (e.getExceptionType()) {
                case ITEM_NOT_FOUND:
                    errorMessage = "Item not found for update.";
                    statusCode = HttpServletResponse.SC_NOT_FOUND;
                    break;
                case ITEM_ALREADY_EXISTS:
                    errorMessage = "Item with this name already exists.";
                    break;
                case ITEM_UPDATE_FAILED:
                    errorMessage = "Failed to update item due to a system error.";
                    statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                    break;
                case DATABASE_ERROR:
                    errorMessage = "A database error occurred during item update.";
                    statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                    break;
                default:
                    errorMessage = "An unexpected business error occurred: " + e.getExceptionType().name();
                    break;
            }
            SendResponse.sendJson(resp, statusCode, Map.of("message", errorMessage));
        }catch (Exception e) {
            logger.log(Level.SEVERE, "Error in PUT /items: " + e.getMessage(), e);
            SendResponse.sendJson(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Map.of("message", "Internal server error."));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String currentUserRole = getUserRoleFromSession(req);
        Integer currentUserId = getUserIdFromSession(req);

        // Only ADMIN with initial ID can delete
        if (!"ADMIN".equals(currentUserRole) || (currentUserId == null || !currentUserId.equals(INITIAL_ADMIN_ID))) {
            SendResponse.sendJson(resp, HttpServletResponse.SC_FORBIDDEN,
                    Map.of("message", "Unauthorized: Only the Initial Admin can delete items."));
            return;
        }

        String idStr = req.getParameter("id");
        if (idStr == null || idStr.trim().isEmpty()) {
            SendResponse.sendJson(resp, HttpServletResponse.SC_BAD_REQUEST, Map.of("message", "Item ID is required for deletion."));
            return;
        }

        try {
            int itemId = Integer.parseInt(idStr);
            boolean deleted = itemService.delete(currentUserId, itemId);

            if (deleted) {
                SendResponse.sendJson(resp, HttpServletResponse.SC_OK, Map.of("message", "Item deleted successfully."));
            } else {
                SendResponse.sendJson(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Map.of("message", "Item deletion failed unexpectedly."));
            }

        } catch (NumberFormatException e) {
            logger.log(Level.WARNING, "Invalid ID format for DELETE /items: " + e.getMessage());
            SendResponse.sendJson(resp, HttpServletResponse.SC_BAD_REQUEST, Map.of("message", "Invalid item ID format."));
        } catch (CustomException e) {
            logger.log(Level.SEVERE, "Error in DELETE /items: " + e.getMessage(), e);
            String errorMessage;
            int statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            switch (e.getExceptionType()) {
                case ITEM_NOT_FOUND:
                    errorMessage = "Item not found for deletion.";
                    statusCode = HttpServletResponse.SC_NOT_FOUND;
                    break;
                case ITEM_DELETION_FAILED:
                    errorMessage = "Failed to delete item due to a system error.";
                    statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                    break;
                case DATABASE_ERROR:
                    errorMessage = "A database error occurred during item deletion.";
                    statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                    break;
                default:
                    errorMessage = "An unexpected business error occurred: " + e.getExceptionType().name();
                    break;
            }
            SendResponse.sendJson(resp, statusCode, Map.of("message", errorMessage));
        }catch (Exception e) {
            logger.log(Level.SEVERE, "Error in DELETE /items: " + e.getMessage(), e);
            SendResponse.sendJson(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Map.of("message", "Internal server error."));
        }
    }

    // Simplified itemDtoToMap without audit fields, used for list/single item responses
    private Map<String, Object> itemDtoToMap(ItemDTO dto) {
        if (dto == null) return null;

        Map<String, Object> map = new HashMap<>();
        map.put("id", dto.getId());
        map.put("itemName", dto.getItemName());
        map.put("unitPrice", dto.getUnitPrice());
        map.put("quantity", dto.getQuantity());
        map.put("createdAt", dto.getCreatedAt());
        map.put("updatedAt", dto.getUpdatedAt());
        map.put("deletedAt", dto.getDeletedAt());

        return map;
    }
}