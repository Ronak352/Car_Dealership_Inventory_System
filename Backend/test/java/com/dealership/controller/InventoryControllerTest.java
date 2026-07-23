package com.dealership.controller;

import com.dealership.dto.response.InventoryResponse;
import com.dealership.dto.response.VehicleResponse;
import com.dealership.enums.InventoryOperation;
import com.dealership.enums.VehicleCategory;
import com.dealership.enums.VehicleStatus;
import com.dealership.security.CustomUserDetailsService;
import com.dealership.security.JwtUtil;
import com.dealership.service.InventoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InventoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InventoryService inventoryService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

  
    @Test
    void shouldIncreaseStock() throws Exception {

        InventoryResponse response = InventoryResponse.builder()
                .vehicleId(1L)
                .operationType(InventoryOperation.ADD)
                .quantity(5)
                .availableQuantity(15)
                .date(LocalDateTime.now())
                .build();


        when(inventoryService.increaseStock(1L,5,1L))
                .thenReturn(response);


        mockMvc.perform(
                post("/api/inventory/increase/1")
                        .param("quantity","5")
                        .param("performedByUserId","1")
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableQuantity").value(15));


        verify(inventoryService)
                .increaseStock(1L,5,1L);
    }
    
    @Test
    void shouldUpdateStock() throws Exception {

        InventoryResponse response =
                InventoryResponse.builder()
                        .vehicleId(1L)
                        .operationType(InventoryOperation.UPDATE)
                        .quantity(20)
                        .availableQuantity(20)
                        .date(LocalDateTime.now())
                        .build();


        when(inventoryService.updateStock(1L,20,1L))
                .thenReturn(response);


        mockMvc.perform(
                put("/api/inventory/update/1")
                        .param("quantity","20")
                        .param("performedByUserId","1")
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableQuantity")
                        .value(20));


        verify(inventoryService)
                .updateStock(1L,20,1L);
    }
    
    
    
    @Test
    void shouldGetAvailableQuantity() throws Exception {

        when(inventoryService.getAvailableQuantity(1L))
                .thenReturn(15);


        mockMvc.perform(
                get("/api/inventory/quantity/1")
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(content().string("15"));


        verify(inventoryService)
                .getAvailableQuantity(1L);
    }
  
    @Test
    void shouldGetInventoryHistory() throws Exception {

        InventoryResponse response =
                InventoryResponse.builder()
                        .id(1L)
                        .vehicleId(1L)
                        .operationType(InventoryOperation.ADD)
                        .quantity(5)
                        .availableQuantity(15)
                        .build();


        when(inventoryService.getInventoryHistory(1L))
                .thenReturn(List.of(response));


        mockMvc.perform(
                get("/api/inventory/history/1")
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].vehicleId").value(1))
                .andExpect(jsonPath("$[0].operationType").value("ADD"));


        verify(inventoryService)
                .getInventoryHistory(1L);
    }

    
    @Test
    void shouldGetLowStockVehicles() throws Exception {

        VehicleResponse response =
                VehicleResponse.builder()
                        .id(1L)
                        .brand("Hyundai")
                        .model("Creta")
                        .variant("SX")
                        .vinNumber("VIN001")
                        .price(BigDecimal.valueOf(1500000))
                        .quantity(3)
                        .category(VehicleCategory.SUV)
                        .status(VehicleStatus.AVAILABLE)
                        .build();


        when(inventoryService.getLowStockVehicles(5))
                .thenReturn(List.of(response));


        mockMvc.perform(
                get("/api/inventory/low-stock")
                        .param("threshold", "5")
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].brand")
                        .value("Hyundai"))
                .andExpect(jsonPath("$[0].quantity")
                        .value(3));


        verify(inventoryService)
                .getLowStockVehicles(5);
    }

    
    
}