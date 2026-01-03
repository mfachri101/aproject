package com.fachri.aproject.service.model;

import java.time.LocalDate;

public record OutstandingLoanInquiryRequest(Long debtorId, LocalDate asOfDate) {
}
