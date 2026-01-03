package com.fachri.aproject.service;

import java.time.LocalDate;

public record DebtorProfileRequest(Long debtorId, LocalDate asOfDate) {
}
