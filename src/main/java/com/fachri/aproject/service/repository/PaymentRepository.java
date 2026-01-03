package com.fachri.aproject.service.repository;

import com.fachri.aproject.generated.tables.Loan;
import com.fachri.aproject.generated.tables.LoanDetail;
import com.fachri.aproject.generated.tables.PaymentHistory;
import com.fachri.aproject.generated.tables.records.PaymentHistoryRecord;
import com.fachri.aproject.service.model.Payment;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import static org.jooq.impl.DSL.sum;

@Repository
public class PaymentRepository {

  @Autowired
  DSLContext dslContext;

  public Payment createPayment(Long debtorId, Payment payment) {
    PaymentHistoryRecord complete = dslContext.transactionResult(
      configuration -> {
        var dsl = configuration.dsl();
        PaymentHistoryRecord storedPaymentHistory = dsl.insertInto(
          PaymentHistory.PAYMENT_HISTORY
        ).columns(
          PaymentHistory.PAYMENT_HISTORY.PAYEDAMOUNT,
          PaymentHistory.PAYMENT_HISTORY.PAYMENTDATE
        ).values(
          payment.amount(),
          payment.paymentTime()
        ).returningResult(
          PaymentHistory.PAYMENT_HISTORY.ID,
          PaymentHistory.PAYMENT_HISTORY.PAYEDAMOUNT,
          PaymentHistory.PAYMENT_HISTORY.PAYMENTDATE
        ).fetchOneInto(PaymentHistoryRecord.class);

        // insert paymentId into loan_detail_payment_history junction table
        dsl.update(LoanDetail.LOAN_DETAIL).set(
          LoanDetail.LOAN_DETAIL.PAYMENTHISTORYID,
          storedPaymentHistory.getId()
        ).where(
          LoanDetail.LOAN_DETAIL.ID.in(
            payment.loanDetailIds()
          )
        ).and(
          LoanDetail.LOAN_DETAIL.LOANID.eq(payment.loanId())
        ).execute();

        // make status to complete if principal fully paid
        /*
         * update loan set status = 'COMPLETE' where id = loanId and principal == select sum(loan_detail.outstandingLoan) from loan_detail left join payment_history on loan_detail.id = payment_history.id
         * */
        dsl.update(
          Loan.LOAN
        ).set(
          Loan.LOAN.STATUS,
          "COMPLETE"
        ).where(
          Loan.LOAN.DEBTORID.eq(debtorId).and(
            Loan.LOAN.ID.eq(payment.loanId())).and(
            Loan.LOAN.PRINCIPAL.greaterOrEqual(
              dsl.select(sum(LoanDetail.LOAN_DETAIL.OUTSTANDINGLOAN))
                .from(LoanDetail.LOAN_DETAIL).leftJoin(PaymentHistory.PAYMENT_HISTORY)
                .on(LoanDetail.LOAN_DETAIL.ID.eq(PaymentHistory.PAYMENT_HISTORY.ID))
                .where(LoanDetail.LOAN_DETAIL.LOANID.eq(payment.loanId())
                )
            )
          )
        ).execute();

        return storedPaymentHistory;
      }
    );

    return new Payment(
      payment.loanId(),
      payment.loanDetailIds(),
      complete.getPaymentdate(),
      complete.getPayedamount()
    );
  }
}
