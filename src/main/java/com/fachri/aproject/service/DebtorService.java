package com.fachri.aproject.service;

import com.fachri.aproject.service.model.Debtor;
import com.fachri.aproject.service.repository.DebtorRepository;
import com.fachri.aproject.service.repository.DebtorWithProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DebtorService {

  @Autowired
  DebtorRepository debtorRepository;

  public List<Debtor> getDebtors() {
    return debtorRepository.getDebtors();
  }

  public Debtor getDebtor(Long debtorId) {
    return debtorRepository.getDebtor(debtorId);
  }

  public Debtor registerBorrower(Debtor request) {
    return debtorRepository.createDebtor(request);
  }

  public DebtorWithProfile getDebtorProfile(DebtorProfileRequest request) {
    return debtorRepository.getDebtorPaymentProfile(request.debtorId(), request.asOfDate());
  }

}
