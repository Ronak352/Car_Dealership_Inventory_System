package com.dealership.enums;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;


class EnumsTest {

    @Test
    void role_hasExactlyFourValues() {
    	assertThat(Role.values()).containsExactly(
                Role.ADMIN, Role.MANAGER, Role.SALESPERSON, Role.CUSTOMER); 
    }

    @Test
    void vehicleCategory_hasExpectedValues() {
        assertThat(VehicleCategory.values()).containsExactly(
                VehicleCategory.SUV, VehicleCategory.SEDAN, VehicleCategory.HATCHBACK,
                VehicleCategory.LUXURY, VehicleCategory.ELECTRIC);
    }

    @Test
    void fuelType_hasExpectedValues() {
        assertThat(FuelType.values()).containsExactly(
                FuelType.PETROL, FuelType.DIESEL, FuelType.CNG,
                FuelType.ELECTRIC, FuelType.HYBRID);
    }

    @Test
    void transmission_hasExpectedValues() {
        assertThat(Transmission.values()).containsExactly(
                Transmission.MANUAL, Transmission.AUTOMATIC);
    }

    @Test
    void vehicleCondition_hasExpectedValues() {
        assertThat(VehicleCondition.values()).containsExactly(
                VehicleCondition.NEW, VehicleCondition.USED);
    }

    @Test
    void vehicleStatus_hasExpectedValues() {
        assertThat(VehicleStatus.values()).containsExactly(
                VehicleStatus.AVAILABLE, VehicleStatus.SOLD,
                VehicleStatus.RESERVED, VehicleStatus.SERVICE);
    }

    @Test
    void paymentMethod_hasExpectedValues() {
        assertThat(PaymentMethod.values()).containsExactly(
                PaymentMethod.CASH, PaymentMethod.CARD,
                PaymentMethod.UPI, PaymentMethod.LOAN);
    }

    @Test
    void paymentStatus_hasExpectedValues() {
        assertThat(PaymentStatus.values()).containsExactly(
                PaymentStatus.PENDING, PaymentStatus.SUCCESS, PaymentStatus.FAILED);
    }

    @Test
    void purchaseStatus_hasExpectedValues() {
        assertThat(PurchaseStatus.values()).containsExactly(
                PurchaseStatus.BOOKED, PurchaseStatus.COMPLETED, PurchaseStatus.CANCELLED);
    }

    @Test
    void loanApprovalStatus_hasExpectedValues() {
        assertThat(LoanApprovalStatus.values()).containsExactly(
                LoanApprovalStatus.PENDING, LoanApprovalStatus.APPROVED, LoanApprovalStatus.REJECTED);
    }

    @Test
    void inventoryOperation_hasExpectedValues() {
        assertThat(InventoryOperation.values()).containsExactly(
                InventoryOperation.ADD, InventoryOperation.REMOVE, InventoryOperation.UPDATE);
    }

    @Test
    void testDriveStatus_hasExpectedValues() {
        assertThat(TestDriveStatus.values()).containsExactly(
                TestDriveStatus.REQUESTED, TestDriveStatus.APPROVED,
                TestDriveStatus.COMPLETED, TestDriveStatus.CANCELLED);
    }
}
