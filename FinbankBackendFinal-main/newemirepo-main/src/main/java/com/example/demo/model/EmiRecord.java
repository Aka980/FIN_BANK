package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "emi_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmiRecord {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long emiId;

// Loan service primary key
	@NotNull(message = "Loan ID is required")
	@Column(name = "loan_id", nullable = false)
	private Long loanId;

// Account number from loan-service
	@NotNull(message = "Account number is required")
	@Column(name = "account_no", nullable = false)
	private Long accountNo;

	@NotNull(message = "Principal amount is required")
	@DecimalMin(value = "1.0", message = "Principal must be greater than 0")
	@Column(name = "principal_amount", nullable = false, precision = 15, scale = 2)
	private BigDecimal principalAmount;

	@NotNull(message = "Rate of interest is required")
	@DecimalMin(value = "0.1", message = "ROI must be greater than 0")
	@DecimalMax(value = "100.0", message = "ROI cannot exceed 100")
	@Column(nullable = false, precision = 5, scale = 2)
	private BigDecimal roi;

	@NotNull(message = "Tenure is required")
	@Min(value = 1, message = "Tenure must be at least 1 month")
	@Max(value = 600, message = "Tenure cannot exceed 600 months")
	@Column(nullable = false)
	private Integer tenure;

	@NotNull(message = "Monthly EMI amount is required")
	@DecimalMin(value = "1.0", message = "EMI must be greater than 0")
	@Column(name = "monthly_emi_amount", nullable = false, precision = 15, scale = 2)
	private BigDecimal monthlyEmiAmount;

	@NotNull(message = "Created date is required")
	@Column(name = "created_date", nullable = false)
	private LocalDate createdDate;
}