package com.dealership.dto.response;

import com.dealership.enums.PaymentMethod;
import com.dealership.enums.PurchaseStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class PurchaseResponseTest {

    @Test
    void shouldCreatePurchaseResponse() {

        PurchaseResponse response = PurchaseResponse.builder()
                .id(1L)
                .customerName("Ronak Rathod")
                .vehicleName("Toyota Fortuner")
                .purchaseDate(LocalDate.now())
                .sellingPrice(new BigDecimal("4500000"))
                .paymentMethod(PaymentMethod.LOAN)
                .purchaseStatus(PurchaseStatus.BOOKED)
                .build();

        assertThat(response.getCustomerName()).isEqualTo("Ronak Rathod");
        assertThat(response.getPurchaseStatus()).isEqualTo(PurchaseStatus.BOOKED);
    }
}
