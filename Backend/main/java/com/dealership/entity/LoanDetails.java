package com.dealership.entity;

import com.dealership.enums.LoanApprovalStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "loan_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class LoanDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_id", nullable = false, unique = true)
    private PurchaseHistory purchase;

    private String bankName;

    private BigDecimal loanAmount;

    private BigDecimal interestRate;

    private Integer tenure;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private LoanApprovalStatus approvalStatus = LoanApprovalStatus.PENDING;
}
