package com.dealership.service;

import com.dealership.dto.response.InventoryResponse;
import com.dealership.dto.response.VehicleResponse;

import java.util.List;

public interface InventoryService {

    /**
     * Increase vehicle stock.
     *
     * @param vehicleId Vehicle ID
     * @param quantity Quantity to add
     * @param performedByUserId User performing the operation
     * @return Inventory log response
     */
    InventoryResponse increaseStock(
            Long vehicleId,
            Integer quantity,
            Long performedByUserId
    );

    /**
     * Decrease vehicle stock.
     *
     * @param vehicleId Vehicle ID
     * @param quantity Quantity to remove
     * @param performedByUserId User performing the operation
     * @return Inventory log response
     */
    InventoryResponse decreaseStock(
            Long vehicleId,
            Integer quantity,
            Long performedByUserId
    );

    /**
     * Set stock to an exact value.
     *
     * @param vehicleId Vehicle ID
     * @param newQuantity New stock quantity
     * @param performedByUserId User performing the operation
     * @return Inventory log response
     */
    InventoryResponse updateStock(
            Long vehicleId,
            Integer newQuantity,
            Long performedByUserId
    );

    /**
     * Get inventory history for a vehicle.
     */
    List<InventoryResponse> getInventoryHistory(Long vehicleId);

    /**
     * Get current available stock.
     */
    Integer getAvailableQuantity(Long vehicleId);

    /**
     * Get all vehicles whose stock is less than or equal to the threshold.
     */
    List<VehicleResponse> getLowStockVehicles(Integer threshold);

}
