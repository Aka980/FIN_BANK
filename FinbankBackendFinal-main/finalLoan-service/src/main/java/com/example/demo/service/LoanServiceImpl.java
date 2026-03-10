package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.entity.LoanApplication;
import com.example.demo.repository.LoanRepository;
import com.example.demo.exception.LoanCancellationException;
import com.example.demo.exception.LoanNotFoundException;
import com.example.demo.exception.UnauthorizedAccessException;
import com.example.demo.feign.CustomerClient;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

	private final LoanRepository loanRepository;
	private final CustomerClient customerClient;

	// @Override
	// public LoanResponseDTO applyLoan(
	// LoanRequestDTO dto,
	//// Long accountNo,
	// String username,
	// String token) {
	//
	// // 🔥 Get PAN from customer-service
	// CustomerProfileDTO profile =
	// customerClient.getMyProfile(token);
	//
	// LoanApplication loan = new LoanApplication();
	//
	// // 🔥 AccountNo from JWT
	// loan.setAccountNo(accountNo);
	//
	// if (profile != null) {
	// loan.setPan(profile.getPan());
	// }
	//
	// loan.setAmount(dto.getAmount());
	// loan.setTenure(dto.getTenure());
	// loan.setLoanType(dto.getLoanType());
	// loan.setUsername(username);
	//
	// // ROI auto from ENUM
	// loan.setRoi(dto.getLoanType().getRoi());
	//
	// loan.setStatus("PENDING");
	//
	// loan.setApplicationDate(LocalDate.now());
	//
	// loanRepository.save(loan);
	//
	// return mapToResponse(loan);
	// }

	@Override
	public LoanResponseDTO applyLoan(LoanRequestDTO dto, Long accountNo, String token) {

		CustomerProfileDTO profile = customerClient.getMyProfile(token);

		LoanApplication loan = new LoanApplication();

		// 🔥 CRITICAL: Store ownership at creation time
		// In this system, username is the accountNo string
		
		loan.setAccountNo(accountNo);

		if (profile != null) {
			loan.setPan(profile.getPan());
		}

		loan.setAmount(dto.getAmount());
		loan.setTenure(dto.getTenure());
		loan.setLoanType(dto.getLoanType());

		// AUTO SET ROI FROM ENUM
		loan.setRoi(dto.getLoanType().getRoi());

		loan.setStatus("PENDING");
		loan.setApplicationDate(LocalDate.now());

		loanRepository.save(loan);

		return mapToResponse(loan);
	}

	@Override
	public LoanResponseDTO updateLoan(Long id, LoanRequestDTO request) {

		LoanApplication existing = loanRepository.findById(id)
				.orElseThrow(() -> new LoanNotFoundException("Loan Not Found"));

		existing.setAmount(request.getAmount());
		existing.setTenure(request.getTenure());
		existing.setLoanType(request.getLoanType());

		return mapToResponse(loanRepository.save(existing));
	}

	@Override
	public void cancelLoan(Long id) {
		LoanApplication loan = loanRepository.findById(id)
				.orElseThrow(() -> new LoanNotFoundException("Loan not found with ID: " + id));

		if ("APPROVED".equalsIgnoreCase(loan.getStatus())) {
			throw new LoanCancellationException("Cannot cancel loan after it is approved.");
		}

		loanRepository.deleteById(id);
	}

	@Override
	public LoanResponseDTO getLoanStatus(Long id) {

		LoanApplication loan = loanRepository.findById(id)
				.orElseThrow(() -> new LoanNotFoundException("Loan Not Found"));

		return mapToResponse(loan);
	}

	@Override
	public List<LoanResponseDTO> getAllLoans() {

		return loanRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
	}

	@Override
	public LoanResponseDTO getLoanById(Long loanId, Long accountNo, String role) {

	    LoanApplication loan = loanRepository.findById(loanId)
	            .orElseThrow(() -> new LoanNotFoundException("Loan not found. Check the Loan ID."));

	    System.out.println("DEBUG: getLoanById - loanId: " + loanId + ", accountNo: " + accountNo + ", role: " + role);

	    String normalizedRole = role == null ? "" : role.trim().toUpperCase();
	    boolean isAdmin = "ADMIN".equals(normalizedRole) || "ROLE_ADMIN".equals(normalizedRole);

	    if (!isAdmin && !loan.getAccountNo().equals(accountNo)) {
	        throw new UnauthorizedAccessException("You are unauthorized to access this loan.");
	    }

	    return mapToResponse(loan);
	}

	private LoanResponseDTO mapToResponse(LoanApplication loan) {
		return new LoanResponseDTO(
				loan.getApplicationNo(),
				loan.getAccountNo(),
				loan.getApplicationDate(),
				loan.getAmount(),
				loan.getPan(),
				loan.getTenure(),
				loan.getRoi(),
				loan.getStatus(),
				loan.getLoanType());
	}

	@Override
	public LoanResponseDTO updateLoanStatus(Long id, String status, String reason) {
		LoanApplication existing = loanRepository.findById(id)
				.orElseThrow(() -> new LoanNotFoundException("Loan Not Found"));

		// If loan application had a reason field we would set it here.
		// For now, we only update the status.
		existing.setStatus(status.toUpperCase());
		return mapToResponse(loanRepository.save(existing));
	}

	@Override
	public List<LoanResponseDTO> getLoansByAccountNo(Long accountNo) {

		List<LoanApplication> loans = loanRepository.findByAccountNo(accountNo);

		return loans.stream()
				.map(this::convertToDTO)
				.toList();
	}

	private LoanResponseDTO convertToDTO(LoanApplication loan) {

		LoanResponseDTO dto = new LoanResponseDTO();

		dto.setApplicationNo(loan.getApplicationNo());
		dto.setAccountNo(loan.getAccountNo());
		dto.setApplicationDate(loan.getApplicationDate());
		dto.setAmount(loan.getAmount());
		dto.setPan(loan.getPan());
		dto.setTenure(loan.getTenure());
		dto.setRoi(loan.getRoi());
		dto.setStatus(loan.getStatus());
		dto.setLoanType(loan.getLoanType());

		return dto;
	}
}
