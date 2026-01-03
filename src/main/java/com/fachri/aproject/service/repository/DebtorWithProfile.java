package com.fachri.aproject.service.repository;

import com.fachri.aproject.service.model.Debtor;

public record DebtorWithProfile(Debtor debtor, String paymentProfileType) {
}
