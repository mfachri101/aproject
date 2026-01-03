package com.fachri.aproject.service.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LoanDetail(Long id,
                         Long debtorId,
                         Long loanId,
                         LocalDate dueDate,
                         BigDecimal outstandingLoan,
                         BigDecimal outstandingInterest,
                         Long paymentDetailId) {
}
