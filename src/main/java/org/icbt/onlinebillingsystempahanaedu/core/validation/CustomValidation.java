package org.icbt.onlinebillingsystempahanaedu.core.validation;

import org.icbt.onlinebillingsystempahanaedu.bill.dto.BillDTO;
import org.icbt.onlinebillingsystempahanaedu.bill.dto.BillDetailsDTO;
import org.icbt.onlinebillingsystempahanaedu.customer.dto.CustomerDTO;
import org.icbt.onlinebillingsystempahanaedu.item.dto.ItemDTO;
import org.icbt.onlinebillingsystempahanaedu.user.dto.UserDTO;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.IntStream;

/**
 * author : Niwanthi
 * date : 8/18/2025
 * time : 6:07 PM
 */
public class CustomValidation {

    private static <T> void validateProperty(Map<String, String> errors, String field, T value, Predicate<T> rule, String message) {
        if (!rule.test(value)) {
            errors.put(field, message);
        }
    }

    public static Map<String, String> validateCustomer(CustomerDTO customerDTO) {
        Map<String, String> errors = new LinkedHashMap<>();

        validateProperty(errors, "accountNumber", customerDTO.getCus_AccountNumber(),
                v -> v != null && !v.trim().isEmpty(),
                "Account number is required.");

        validateProperty(errors, "name", customerDTO.getCus_Name(),
                v -> v != null && !v.trim().isEmpty(),
                "Name cannot be blank.");

        validateProperty(errors, "phone", customerDTO.getCus_Mobile(),
                v -> v != null && v.matches("^\\d{10}$"),
                "Phone number must be exactly 10 digits.");

        if (customerDTO.getUnits_consumed() < 0) {
            errors.put("unitsConsumed", "Units consumed must be zero or positive.");
        }
        return errors;
    }

    public static Map<String, String> validateBill(BillDTO billDTO) {
        Map<String, String> errors = new HashMap<>();

        if (billDTO.getCustomer_id() <= 0) {
            errors.put("customerId", "A valid customer must be selected.");
        }

        List<BillDetailsDTO> details = Optional.ofNullable(billDTO.getBillDetails()).orElse(Collections.emptyList());

        if (details.isEmpty()) {
            errors.put("items", "A bill must contain at least one item.");
        } else {
            IntStream.range(0, details.size()).forEach(i -> {
                BillDetailsDTO detail = details.get(i);
                if (detail.getItem_id() <= 0) {
                    errors.put("item[" + i + "].id", "An invalid item was included in the bill.");
                }
                if (detail.getUnits() <= 0) {
                    errors.put("item[" + i + "].units", "Item quantity must be at least 1.");
                }
            });
        }
        return errors;
    }

    public static Map<String, String> validateUser(UserDTO userDTO) {
        Map<String, String> errors = new LinkedHashMap<>();

        validateProperty(errors, "username", userDTO.getUsername(),
                v -> v != null && v.trim().length() >= 3,
                "Username must be at least 3 characters.");

        if (userDTO.getId() == 0) {
            validateProperty(errors, "password", userDTO.getPassword(),
                    v -> v != null && v.length() >= 6,
                    "Password must be at least 6 characters.");
        }

        validateProperty(errors, "role", userDTO.getRole(),
                r -> r != null && !r.toString().trim().isEmpty(),
                "A user role must be selected.");

        return errors;
    }

    public static Map<String, String> validateItem(ItemDTO itemDTO) {
        Map<String, String> errors = new LinkedHashMap<>();

        validateProperty(errors, "name", itemDTO.getItemName(),
                v -> v != null && v.trim().length() >= 3,
                "Item name must be at least 3 characters.");

        validateProperty(errors, "unitPrice", itemDTO.getUnitPrice(),
                price -> price != null && price >= 0.0,
                "Unit price must be zero or a positive value.");

        validateProperty(errors, "quantity", itemDTO.getQuantity(),
                qty -> qty != null && qty >= 0,
                "Stock quantity cannot be negative.");

        return errors;
    }
}
