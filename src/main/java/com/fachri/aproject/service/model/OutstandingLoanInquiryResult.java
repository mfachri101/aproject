package com.fachri.aproject.service.model;

import java.math.BigDecimal;
import java.util.List;

public record OutstandingLoanInquiryResult(String result, List<LoanDetail> loanDetail, BigDecimal outstandingAmount) {
}
