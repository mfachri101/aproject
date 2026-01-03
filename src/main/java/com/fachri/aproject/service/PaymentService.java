package com.fachri.aproject.service;

import com.fachri.aproject.service.model.LoanDetail;
import com.fachri.aproject.service.model.OutstandingLoanInquiryRequest;
import com.fachri.aproject.service.model.OutstandingLoanInquiryResult;
import com.fachri.aproject.service.model.Payment;
import com.fachri.aproject.service.model.PaymentRequest;
import com.fachri.aproject.service.model.PaymentResult;
import com.fachri.aproject.service.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class PaymentService {

  private static final Logger log = LoggerFactory.getLogger(PaymentService.class);
  @Autowired
  LoanService loanService;

  @Autowired
  PaymentRepository paymentRepository;

  public PaymentResult makePayment(PaymentRequest request, LocalDate asOfDate) {
    OutstandingLoanInquiryResult outstandingLoan = loanService.getOutstandingLoan(new OutstandingLoanInquiryRequest(
      request.debtorId(), asOfDate
    ));
    validatePaymentAmount(request, outstandingLoan);

    List<LoanDetail> loanDetails = outstandingLoan.loanDetail();

    Payment payment = paymentRepository.createPayment(
      request.debtorId(),
      new Payment(
        loanDetails.getFirst().loanId(),
        loanDetails.stream().map(LoanDetail::id).toList(),
        request.paymentTime(),
        request.amount()
      )
    );
    if (payment == null) {
      // todo: implement better error handling and error response
      log.error("Failed to create payment for debtorId {}", request.debtorId());
      throw new IllegalStateException("Failed to create payment");
    }
    return new PaymentResult(
      "OK", payment
    );
  }

  private void validatePaymentAmount(PaymentRequest request, OutstandingLoanInquiryResult outstandingLoan) {
    var totalOutstanding = outstandingLoan.loanDetail().stream().map(
      x -> x.outstandingLoan().add(x.outstandingInterest())
    ).reduce(BigDecimal.ZERO, BigDecimal::add);
    if (totalOutstanding.equals(BigDecimal.ZERO)) {
      log.error("No outstanding loan for debtorId {}", request.debtorId());
      throw new IllegalArgumentException("No outstanding loan");
    }
    if (!request.amount().equals(totalOutstanding)) {
      log.error("Payment amount {} not equals to total outstanding {}", request.amount(), totalOutstanding);
      throw new IllegalArgumentException("Payment amount not equals to total outstanding");
    }
  }

}
