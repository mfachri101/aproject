package com.fachri.aproject.service.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record Payment(Long loanId, List<Long> loanDetailIds, LocalDateTime paymentTime, BigDecimal amount) {
}
