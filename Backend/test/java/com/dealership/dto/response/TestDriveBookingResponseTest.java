package com.dealership.dto.response;

import com.dealership.enums.TestDriveStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class TestDriveBookingResponseTest {

    @Test
    void shouldCreateTestDriveBookingResponse() {

        TestDriveBookingResponse response =
                TestDriveBookingResponse.builder()
                .id(1L)
                .customerName("Ronak Rathod")
                .vehicleName("Toyota Fortuner")
                .bookingDate(LocalDate.now())
                .status(TestDriveStatus.REQUESTED)
                .build();


        assertThat(response.getCustomerName())
                .isEqualTo("Ronak Rathod");

        assertThat(response.getStatus())
                .isEqualTo(TestDriveStatus.REQUESTED);
    }
}