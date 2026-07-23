package com.dealership.entity;

import com.dealership.enums.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;


class DomainEntitiesTest {

    @Test
    void user_builderPopulatesAllFields() {
        LocalDateTime now = LocalDateTime.now();
        User user = User.builder()
                .id(1L)
                .firstName("Ronak")
                .lastName("Patel")
                .email("ronak@example.com")
                .phone("9999999999")
                .password("hashed-pw")
                .role(Role.ADMIN)
                .status("ACTIVE")
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getEmail()).isEqualTo("ronak@example.com");
        assertThat(user.getRole()).isEqualTo(Role.ADMIN);
        assertThat(user.getCreatedAt()).isEqualTo(now);
    }

    @Test
    void vehicle_builderPopulatesAllFields() {
        Vehicle vehicle = Vehicle.builder()
                .id(1L)
                .brand("Toyota")
                .model("Fortuner")
                .variant("Legender")
                .category(VehicleCategory.SUV)
                .fuelType(FuelType.DIESEL)
                .transmission(Transmission.AUTOMATIC)
                .manufacturingYear(2026)
                .color("White")
                .engineNumber("ENG12345")
                .vinNumber("VIN12345")
                .price(new BigDecimal("4500000"))
                .discount(new BigDecimal("50000"))
                .quantity(5)
                .condition(VehicleCondition.NEW)
                .status(VehicleStatus.AVAILABLE)
                .createdAt(LocalDateTime.now())
                .build();

        assertThat(vehicle.getVinNumber()).isEqualTo("VIN12345");
        assertThat(vehicle.getCategory()).isEqualTo(VehicleCategory.SUV);
        assertThat(vehicle.getStatus()).isEqualTo(VehicleStatus.AVAILABLE);
        assertThat(vehicle.getQuantity()).isEqualTo(5);
    }

    @Test
    void vehicleImage_linksToVehicle() {
        Vehicle vehicle = Vehicle.builder().id(1L).brand("Toyota").build();
        VehicleImage image = VehicleImage.builder()
                .id(1L)
                .vehicle(vehicle)
                .imageUrl("https://cdn.example.com/fortuner.jpg")
                .build();

        assertThat(image.getVehicle()).isEqualTo(vehicle);
        assertThat(image.getImageUrl()).contains("fortuner.jpg");
    }

    @Test
    void customer_linksToUser() {
        User user = User.builder().id(1L).build();
        Customer customer = Customer.builder()
                .id(1L)
                .user(user)
                .address("221B Baker Street")
                .city("Ahmedabad")
                .state("Gujarat")
                .pincode("380001")
                .build();

        assertThat(customer.getUser()).isEqualTo(user);
        assertThat(customer.getCity()).isEqualTo("Ahmedabad");
    }

    
    @Test
    void employee_linksToUser() {
        User user = User.builder().id(2L).build();
        Employee employee = Employee.builder()
                .id(1L)
                .user(user)
                .employeeCode("EMP001")
                .joiningDate(LocalDate.now())
                .salary(new BigDecimal("45000"))
                .build();

        assertThat(employee.getEmployeeCode()).isEqualTo("EMP001");
        assertThat(employee.getUser()).isEqualTo(user);
    }

    @Test
    void purchaseHistory_linksCustomerVehicleAndSalesperson() {
        Customer customer = Customer.builder().id(1L).build();
        Vehicle vehicle = Vehicle.builder().id(1L).build();
        Employee salesperson = Employee.builder().id(1L).build();

        PurchaseHistory purchase = PurchaseHistory.builder()
                .id(1L)
                .customer(customer)
                .vehicle(vehicle)
                .salesperson(salesperson)
                .purchaseDate(LocalDate.now())
                .deliveryDate(LocalDate.now().plusDays(7))
                .sellingPrice(new BigDecimal("4400000"))
                .paymentMethod(PaymentMethod.LOAN)
                .purchaseStatus(PurchaseStatus.BOOKED)
                .build();

        assertThat(purchase.getCustomer()).isEqualTo(customer);
        assertThat(purchase.getVehicle()).isEqualTo(vehicle);
        assertThat(purchase.getPurchaseStatus()).isEqualTo(PurchaseStatus.BOOKED);
    }

    @Test
    void paymentHistory_linksToPurchase() {
        PurchaseHistory purchase = PurchaseHistory.builder().id(1L).build();
        PaymentHistory payment = PaymentHistory.builder()
                .id(1L)
                .purchase(purchase)
                .amount(new BigDecimal("100000"))
                .paymentMethod(PaymentMethod.UPI)
                .paymentStatus(PaymentStatus.SUCCESS)
                .transactionId("TXN123")
                .paymentDate(LocalDateTime.now())
                .build();

        assertThat(payment.getPurchase()).isEqualTo(purchase);
        assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.SUCCESS);
    }

    @Test
    void loanDetails_linksToPurchase() {
        PurchaseHistory purchase = PurchaseHistory.builder().id(1L).build();
        LoanDetails loan = LoanDetails.builder()
                .id(1L)
                .purchase(purchase)
                .bankName("HDFC Bank")
                .loanAmount(new BigDecimal("3000000"))
                .interestRate(new BigDecimal("8.5"))
                .tenure(60)
                .approvalStatus(LoanApprovalStatus.PENDING)
                .build();

        assertThat(loan.getBankName()).isEqualTo("HDFC Bank");
        assertThat(loan.getApprovalStatus()).isEqualTo(LoanApprovalStatus.PENDING);
    }

    @Test
    void inventoryLog_linksToVehicleAndUser() {
        Vehicle vehicle = Vehicle.builder().id(1L).build();
        User performedBy = User.builder().id(1L).build();
        InventoryLog log = InventoryLog.builder()
                .id(1L)
                .vehicle(vehicle)
                .operationType(InventoryOperation.ADD)
                .quantity(10)
                .date(LocalDateTime.now())
                .performedBy(performedBy)
                .build();

        assertThat(log.getOperationType()).isEqualTo(InventoryOperation.ADD);
        assertThat(log.getPerformedBy()).isEqualTo(performedBy);
    }

    @Test
    void testDriveBooking_linksCustomerVehicleAndSalesperson() {
        Customer customer = Customer.builder().id(1L).build();
        Vehicle vehicle = Vehicle.builder().id(1L).build();
        Employee salesperson = Employee.builder().id(1L).build();

        TestDriveBooking booking = TestDriveBooking.builder()
                .id(1L)
                .customer(customer)
                .vehicle(vehicle)
                .salesperson(salesperson)
                .bookingDate(LocalDate.now())
                .testDriveDate(LocalDate.now().plusDays(2))
                .status(TestDriveStatus.REQUESTED)
                .build();

        assertThat(booking.getStatus()).isEqualTo(TestDriveStatus.REQUESTED);
        assertThat(booking.getSalesperson()).isEqualTo(salesperson);
    }

    @Test
    void wishlist_linksCustomerAndVehicle() {
        Customer customer = Customer.builder().id(1L).build();
        Vehicle vehicle = Vehicle.builder().id(1L).build();

        Wishlist wishlist = Wishlist.builder()
                .id(1L)
                .customer(customer)
                .vehicle(vehicle)
                .createdAt(LocalDateTime.now())
                .build();

        assertThat(wishlist.getCustomer()).isEqualTo(customer);
        assertThat(wishlist.getVehicle()).isEqualTo(vehicle);
    }

    @Test
    void refreshToken_linksToUser() {
        User user = User.builder().id(1L).build();
        RefreshToken refreshToken = RefreshToken.builder()
                .id(1L)
                .user(user)
                .token("some-refresh-token")
                .expiryDate(LocalDateTime.now().plusDays(7))
                .build();

        assertThat(refreshToken.getUser()).isEqualTo(user);
        assertThat(refreshToken.getToken()).isEqualTo("some-refresh-token");
    }
}
