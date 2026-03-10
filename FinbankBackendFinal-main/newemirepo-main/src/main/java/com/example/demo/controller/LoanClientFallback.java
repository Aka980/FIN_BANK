package com.example.demo.controller;

import com.example.demo.Dto.LoanResponse;
import org.springframework.stereotype.Component;

@Component
public class LoanClientFallback implements feignclient {

    @Override
	public LoanResponse getLoanById( String token, Long loanId) {
        throw new RuntimeException(
            "Loan service is unavailable. Please try again later."
        );
    }

	@Override
	public LoanResponse getLoanStatus(Long id) {
		throw new RuntimeException(
	            "Loan service is unavailable. Please try again later."
	      );
	}
}