package org.icbt.onlinebillingsystempahanaedu.bill.mapper;

import org.icbt.onlinebillingsystempahanaedu.bill.dto.BillDTO;
import org.icbt.onlinebillingsystempahanaedu.bill.dto.BillDetailsDTO;
import org.icbt.onlinebillingsystempahanaedu.bill.entity.BillDetailsEntity;
import org.icbt.onlinebillingsystempahanaedu.bill.entity.BillEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * author : Niwanthi
 * date : 8/16/2025
 * time : 2:37 PM
 */
public class BillMapper {

    private BillMapper() {
    }

    public static BillEntity toEntity(BillDTO billDTO) {
        if (billDTO == null) {
            return null;
        }

        BillEntity billEntity = new BillEntity();
        billEntity.setId(billDTO.getId());
        billEntity.setCustomer_id(billDTO.getCustomer_id());
        billEntity.setTotal_amount(billDTO.getTotal_amount());

        if (billDTO.getBillDetails() != null) {
            List<BillDetailsEntity> detailEntities = new ArrayList<>();
            for (BillDetailsDTO detailDto : billDTO.getBillDetails()) {
                detailEntities.add(toEntity(detailDto));
            }
            billEntity.setBillDetails(detailEntities);
        }

        billEntity.setCreated_at(billDTO.getCreated_at());
        billEntity.setDeleted_at(billDTO.getDeleted_at());

        return billEntity;
    }

    public static BillDTO toDTO(BillEntity billEntity) {
        if (billEntity == null) {
            return null;
        }

        BillDTO billDTO = new BillDTO();
        billDTO.setId(billEntity.getId());
        billDTO.setCustomer_id(billEntity.getCustomer_id());
        billDTO.setTotal_amount(billEntity.getTotal_amount());
        billDTO.setCreated_at(billEntity.getCreated_at());
        billDTO.setDeleted_at(billEntity.getDeleted_at());

        return billDTO;
    }

    public static BillDetailsEntity toEntity(BillDetailsDTO billDetailsDTO) {
        if (billDetailsDTO == null) {
            return null;
        }

        BillDetailsEntity billDetailsEntity = new BillDetailsEntity();
        billDetailsEntity.setId(billDetailsDTO.getId());
        billDetailsEntity.setBill_id(billDetailsDTO.getBill_id());
        billDetailsEntity.setItem_id(billDetailsDTO.getItem_id());
        billDetailsEntity.setItem_name_at_sale(billDetailsDTO.getItem_name_at_sale());
        billDetailsEntity.setUnit_price_at_sale(billDetailsDTO.getUnit_price_at_sale());
        billDetailsEntity.setUnits(billDetailsDTO.getUnits());
        billDetailsEntity.setTotal(billDetailsDTO.getTotal());
        billDetailsEntity.setCreated_at(billDetailsDTO.getCreated_at());
        billDetailsEntity.setDeleted_at(billDetailsDTO.getDeleted_at());

        return billDetailsEntity;
    }

    public static BillDetailsDTO toDTO(BillDetailsEntity billDetailsEntity) {
        if (billDetailsEntity == null) {
            return null;
        }

        BillDetailsDTO billDetailsDTO = new BillDetailsDTO();
        billDetailsDTO.setId(billDetailsEntity.getId());
        billDetailsDTO.setBill_id(billDetailsEntity.getBill_id());
        billDetailsDTO.setItem_id(billDetailsEntity.getItem_id());
        billDetailsDTO.setItem_name_at_sale(billDetailsEntity.getItem_name_at_sale());
        billDetailsDTO.setUnit_price_at_sale(billDetailsEntity.getUnit_price_at_sale());
        billDetailsDTO.setUnits(billDetailsEntity.getUnits());
        billDetailsDTO.setTotal(billDetailsEntity.getTotal());
        billDetailsDTO.setCreated_at(billDetailsEntity.getCreated_at());
        billDetailsDTO.setDeleted_at(billDetailsEntity.getDeleted_at());

        return billDetailsDTO;
    }

    public static List<BillDTO> toDTOList(List<BillEntity> billEntity) {
        List<BillDTO> dtos = new ArrayList<>();
        if (billEntity != null) {
            for (BillEntity entity : billEntity) {
                dtos.add(toDTO(entity));
            }
        }
        return dtos;
    }

    public static List<BillDetailsDTO> ToDTOListDetails(List<BillDetailsEntity> billDetailsEntityList) {
        List<BillDetailsDTO> dtos = new ArrayList<>();
        if (billDetailsEntityList != null) {
            for (BillDetailsEntity billDetailsEntity : billDetailsEntityList) {
                dtos.add(toDTO(billDetailsEntity));
            }
        }
        return dtos;
    }

    public static Map<String, Object> mapToDetails(BillDetailsDTO billDetailsDTO) {
        if (billDetailsDTO == null) return null;
        Map<String, Object> map = new HashMap<>();
        map.put("id", billDetailsDTO.getId());
        map.put("bill_id", billDetailsDTO.getBill_id());
        map.put("item_id", billDetailsDTO.getItem_id());
        map.put("item_name_at_sale", billDetailsDTO.getItem_name_at_sale());
        map.put("unit_price_at_sale", billDetailsDTO.getUnit_price_at_sale());
        map.put("units", billDetailsDTO.getUnits());
        map.put("total", billDetailsDTO.getTotal());

        return map;
    }

    public static Map<String, Object> mapToBill(BillDTO billDTO) {
        if (billDTO == null) return null;
        Map<String, Object> map = new HashMap<>();
        map.put("id", billDTO.getId());
        map.put("customer_id", billDTO.getCustomer_id());
        map.put("cus_AccountNumber", billDTO.getCus_AccountNumber());
        map.put("cus_Name", billDTO.getCus_Name());
        map.put("total_amount", billDTO.getTotal_amount());

        if (billDTO.getBillDetails() != null) {
            List<Map<String, Object>> details = billDTO.getBillDetails().stream()
                    .map(BillMapper::mapToDetails)
                    .collect(Collectors.toList());
            map.put("details", details);
        } else {
            map.put("details", new ArrayList<>());
        }
        return map;
    }

    public static Map<String, Object> toMap(BillDTO dto) {
        if (dto == null) return null;
        Map<String, Object> map = new HashMap<>();
        map.put("id", dto.getId());
        map.put("customerId", dto.getCustomer_id());
        map.put("customerAccountNumber", dto.getCus_AccountNumber());
        map.put("customerName", dto.getCus_Name());
        map.put("totalAmount", dto.getTotal_amount());
        map.put("generatedAt", dto.getCreated_at());

        if (dto.getBillDetails() != null) {
            List<Map<String, Object>> detailMaps = dto.getBillDetails().stream()
                    .map(BillMapper::detailToMap)
                    .collect(Collectors.toList());
            map.put("details", detailMaps);
        } else {
            map.put("details", new ArrayList<>());
        }
        return map;
    }

    public static Map<String, Object> detailToMap(BillDetailsDTO dto) {
        if (dto == null) return null;
        return Map.of(
                "itemNameAtSale", dto.getItem_name_at_sale(),
                "unitPriceAtSale", dto.getUnit_price_at_sale(),
                "units", dto.getUnits(),
                "total", dto.getTotal()
        );
    }
}