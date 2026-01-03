package com.fachri.aproject.service.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record Loan(Long id,
                   Long debtorId,
                   LocalDate startDate,
                   int term,
                   String frequency,
                   BigDecimal rate,
                   String rateType,
                   BigDecimal principal,
                   String status) {
}
