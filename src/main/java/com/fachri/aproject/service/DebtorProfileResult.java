package com.fachri.aproject.service;

import com.fachri.aproject.service.model.Debtor;

public record DebtorProfileResult(String status, Debtor debtor, String debtorProfile) {
}
