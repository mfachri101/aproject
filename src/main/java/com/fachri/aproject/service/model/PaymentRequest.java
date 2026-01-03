package com.fachri.aproject.service.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentRequest(Long debtorId, BigDecimal amount, LocalDateTime paymentTime) {
}
