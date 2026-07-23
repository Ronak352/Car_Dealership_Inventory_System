package com.dealership.controller;


import com.dealership.dto.request.TestDriveBookingRequest;
import com.dealership.dto.response.TestDriveBookingResponse;
import com.dealership.enums.TestDriveStatus;
import com.dealership.service.TestDriveService;


import com.fasterxml.jackson.databind.ObjectMapper;


import org.junit.jupiter.api.Test;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;


import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;


import org.springframework.test.web.servlet.MockMvc;


import java.time.LocalDate;


import static org.mockito.ArgumentMatchers.any;


import static org.mockito.Mockito.when;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



@WebMvcTest(
        controllers = TestDriveController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = {
                                com.dealership.security.JwtAuthenticationFilter.class
                        }
                )
        }
)
@AutoConfigureMockMvc(addFilters = false)
class TestDriveControllerTest {



    @Autowired
    private MockMvc mockMvc;



    @MockBean
    private TestDriveService testDriveService;



    @Autowired
    private ObjectMapper objectMapper;







    @Test
    void shouldCreateBooking() throws Exception {



        TestDriveBookingResponse response =
                TestDriveBookingResponse.builder()
                .id(1L)
                .status(TestDriveStatus.REQUESTED)
                .build();



        when(testDriveService.createBooking(any()))
                .thenReturn(response);



        TestDriveBookingRequest request =
                TestDriveBookingRequest.builder()
                .customerId(1L)
                .vehicleId(1L)
                .bookingDate(LocalDate.now())
                .testDriveDate(LocalDate.now())
                .build();



        mockMvc.perform(
                post("/api/test-drives")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(request)
                )
        )
        .andExpect(status().isCreated());

    }









    @Test
    void shouldGetBookingById() throws Exception {



        TestDriveBookingResponse response =
                TestDriveBookingResponse.builder()
                .id(1L)
                .status(TestDriveStatus.REQUESTED)
                .build();



        when(testDriveService.getBookingById(1L))
                .thenReturn(response);



        mockMvc.perform(
                get("/api/test-drives/1")
        )
        .andExpect(status().isOk());

    }



}