package com.fachri.aproject.service;

import com.fachri.aproject.service.model.Debtor;
import com.fachri.aproject.service.model.Loan;
import com.fachri.aproject.service.model.LoanCreationRequest;
import com.fachri.aproject.service.model.LoanCreationResult;
import com.fachri.aproject.service.model.LoanDetail;
import com.fachri.aproject.service.model.OutstandingLoanInquiryRequest;
import com.fachri.aproject.service.model.OutstandingLoanInquiryResult;
import com.fachri.aproject.service.repository.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class LoanService {

  @Autowired
  LoanRepository loanRepository;

  @Autowired
  DebtorService debtorService;

  @Autowired
  FrequencyService frequencyService;

  @Autowired
  RateService rateService;

  public LoanCreationResult createLoan(LoanCreationRequest loanCreationRequest) {
    Loan loanRequest = loanCreationRequest.loan();
    loanRequest = new Loan(
      null,
      loanCreationRequest.debtorId(),
      loanRequest.startDate(),
      loanRequest.term(),
      loanRequest.frequency(),
      loanRequest.rate(),
      loanRequest.rateType(),
      loanRequest.principal(),
      "OPEN"
    );
    List<LoanDetail> loanDetails = generateLoanDetails(loanRequest);

    Loan loanResult = loanRepository.insertLoan(
      loanRequest.debtorId(),
      loanRequest,
      loanDetails
    );
    return new LoanCreationResult("OK", loanResult);
  }

  // generate loan details based on loan request info
  private List<LoanDetail> generateLoanDetails(Loan loan) {
    List<LocalDate> dueDates = frequencyService.generatePaymentDates(LocalDate.now(), loan.term(), loan.frequency());
    List<RateService.LoanRate> loanRates = rateService.calculateLoanRates(loan.principal(), loan.rate(), loan.term(), loan.rateType());
    List<LoanDetail> loanDetails = new ArrayList<>(loan.term());
    for (int i = 0; i < loan.term(); i++) {
      RateService.LoanRate loanRate = loanRates.get(i);
      LoanDetail loanDetail = new LoanDetail(null, loan.debtorId(), null, dueDates.get(i), loanRate.principal(), loanRate.interest(), null);
      loanDetails.add(loanDetail);
    }
    return loanDetails;
  }

  public OutstandingLoanInquiryResult getOutstandingLoan(OutstandingLoanInquiryRequest request) {
    Debtor debtor = debtorService.getDebtor(request.debtorId());
    if (debtor == null) {
      return new OutstandingLoanInquiryResult("DEBTOR_NOT_FOUND", null, BigDecimal.ZERO);
    }
    List<LoanDetail> outstandingLoanDetail = loanRepository.getOutstandingLoanDetail(request.debtorId(), request.asOfDate());
    if (outstandingLoanDetail == null || outstandingLoanDetail.isEmpty()) {
      return new OutstandingLoanInquiryResult("OK", null, BigDecimal.ZERO);
    }
    BigDecimal totalOutstandingLoan = outstandingLoanDetail.stream().map(
      x -> x.outstandingLoan().add(x.outstandingInterest())
    ).reduce(BigDecimal.ZERO, BigDecimal::add);
    return new OutstandingLoanInquiryResult("OK", outstandingLoanDetail, totalOutstandingLoan);
  }
}
