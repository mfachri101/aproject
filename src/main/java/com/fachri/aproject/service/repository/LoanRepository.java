package com.fachri.aproject.service.repository;

import com.fachri.aproject.service.model.Loan;
import com.fachri.aproject.service.model.LoanDetail;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Query;
import org.jooq.Record;
import org.jooq.SelectJoinStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static com.fachri.aproject.generated.tables.Loan.LOAN;
import static com.fachri.aproject.generated.tables.LoanDetail.LOAN_DETAIL;
import static com.fachri.aproject.generated.tables.PaymentHistory.PAYMENT_HISTORY;

@Repository
public class LoanRepository {

  @Autowired
  DSLContext dslContext;

  public Loan getLoanById(Long debtorId, Long loanId) {
    return dslContext.select().from(LOAN)
      .where(
        LOAN.DEBTORID.eq(debtorId)
          .and(LOAN.ID.eq(loanId))
      ).fetchOneInto(Loan.class);
  }

  /**
   * Insert a new loan along with its loan details in a single transaction.
   *
   * @param debtorId    The ID of the debtor.
   * @param loan        The loan information to be inserted.
   * @param loanDetails The list of loan details associated with the loan.
   * @return The inserted Loan object with generated ID.
   *
   */
  public Loan insertLoan(Long debtorId, Loan loan, List<LoanDetail> loanDetails) {
    Long createdLoanId = dslContext.transactionResult(
      configuration -> {
        DSLContext using = configuration.dsl();
        Long loanId = using.insertInto(
          LOAN
        ).columns(
          LOAN.DEBTORID,
          LOAN.STARTDATE,
          LOAN.TERM,
          LOAN.FREQUENCIES,
          LOAN.RATE,
          LOAN.RATETYPE,
          LOAN.PRINCIPAL,
          LOAN.STATUS
        ).values(
          debtorId,
          loan.startDate(),
          loan.term(),
          loan.frequency(),
          loan.rate(),
          loan.rateType(),
          loan.principal(),
          loan.status()
        ).returningResult(
          LOAN.ID
        ).fetchOne(LOAN.ID);
        for (LoanDetail loanDetail : loanDetails) {
          Query insertLoanDetailQuery = using.insertInto(
            LOAN_DETAIL
          ).columns(
            LOAN_DETAIL.LOANID,
            LOAN_DETAIL.DEBTORID,
            LOAN_DETAIL.DUEDATE,
            LOAN_DETAIL.OUTSTANDINGLOAN,
            LOAN_DETAIL.OUTSTANDINGINTEREST
          ).values(
            loanId,
            debtorId,
            loanDetail.dueDate(),
            loanDetail.outstandingLoan(),
            loanDetail.outstandingInterest()
          );
          insertLoanDetailQuery.execute();
        }
        return loanId;
      }
    );
    if (createdLoanId == null) {
      throw new RuntimeException("Failed to insert loan");
    }
    return getLoanById(
      debtorId,
      createdLoanId
    );
  }

  /**
   * Get the outstanding loan detail for a debtor as of a specific date.
   * This method retrieves the earliest due loan detail that has no payment history
   * and whose due date is less than or equal to the specified date.
   *
   * @param debtorId The ID of the debtor.
   * @param asOf     The date to check for outstanding loans.
   * @return The outstanding LoanDetail or null if none found.
   *
   */
  public List<LoanDetail> getOutstandingLoanDetail(Long debtorId, LocalDate asOf) {
    // select the earliest due loan detail that has no payment history and due date is less than or equal to asOf date
    // left anti join products with payment history to find loan details with no payment history
    SelectJoinStep<Record> selectFrom = dslContext.select()
      .from(LOAN_DETAIL);
    Condition condition = LOAN_DETAIL.DEBTORID.eq(debtorId)
      .and(LOAN_DETAIL.PAYMENTHISTORYID.isNull());
    if (asOf != null) {
      condition = condition.and(LOAN_DETAIL.DUEDATE.lessOrEqual(asOf));
    }
    return selectFrom.where(
      condition
    ).orderBy(
      LOAN_DETAIL.DUEDATE.asc()
    ).fetchInto(
      LoanDetail.class
    );
  }

}
