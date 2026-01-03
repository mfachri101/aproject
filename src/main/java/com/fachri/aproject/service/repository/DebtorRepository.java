package com.fachri.aproject.service.repository;

import com.fachri.aproject.generated.tables.records.DebtorRecord;
import com.fachri.aproject.service.model.Debtor;
import org.jooq.CommonTableExpression;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record2;
import org.jooq.SelectHavingStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static com.fachri.aproject.generated.tables.LoanDetail.LOAN_DETAIL;
import static com.fachri.aproject.generated.tables.PaymentHistory.PAYMENT_HISTORY;
import static org.jooq.impl.DSL.count;
import static org.jooq.impl.DSL.name;
import static org.jooq.impl.DSL.select;
import static org.jooq.impl.DSL.val;

@Repository
public class DebtorRepository {

  @Autowired
  DSLContext dslContext;

  public Debtor createDebtor(Debtor debtor) {
    var insertStep = dslContext.insertInto(
      com.fachri.aproject.generated.tables.Debtor.DEBTOR
    ).columns(
      com.fachri.aproject.generated.tables.Debtor.DEBTOR.NAME,
      com.fachri.aproject.generated.tables.Debtor.DEBTOR.DOCUMENTID,
      com.fachri.aproject.generated.tables.Debtor.DEBTOR.EMAIL
    ).values(
      debtor.name(),
      debtor.documentId(),
      debtor.email()
    );
    Debtor insertResult = insertStep.returningResult(
      com.fachri.aproject.generated.tables.Debtor.DEBTOR.ID,
      com.fachri.aproject.generated.tables.Debtor.DEBTOR.NAME,
      com.fachri.aproject.generated.tables.Debtor.DEBTOR.DOCUMENTID,
      com.fachri.aproject.generated.tables.Debtor.DEBTOR.EMAIL
    ).fetchOneInto(Debtor.class);
    if (insertResult == null) {
      throw new RuntimeException("Failed to insert debtor");
    }
    return insertResult;
  }

  public DebtorWithProfile getDebtorPaymentProfile(Long debtorId, LocalDate asOf) {
    // Count number of outstanding loans for the debtor
    SelectHavingStep<Record2<Long, Integer>> debtorIdNumOutStandingCteQuery = select(
      val(debtorId).as("debtor_id"), count().as("num_outstanding_loan")
    ).from(
      LOAN_DETAIL
    ).where(
      LOAN_DETAIL.DEBTORID.eq(debtorId)
        .and(LOAN_DETAIL.DUEDATE.le(asOf))
        .and(LOAN_DETAIL.PAYMENTHISTORYID.isNull())
    );

    CommonTableExpression<Record2<Long, Integer>> cte = name("outstanding_loan_cte").fields("debtor_id", "num_outstanding_loan").as(
      debtorIdNumOutStandingCteQuery
    );

    Record record = dslContext.with(
      cte
    ).select().from(com.fachri.aproject.generated.tables.Debtor.DEBTOR).join(cte).on(
      cte.field("debtor_id", Long.class).eq(com.fachri.aproject.generated.tables.Debtor.DEBTOR.ID)
    ).where(
      com.fachri.aproject.generated.tables.Debtor.DEBTOR.ID.eq(debtorId)
    ).fetchOne();

    if (record == null) {
      return null;
    }

    DebtorRecord debtorRecord = record.into(DebtorRecord.class);
    Integer numOutstandingLoan = record.get("num_outstanding_loan", Integer.class);
    String paymentProfileType = "REGULAR";

    // determine payment profile type, if numOutstandingLoan > 2 then DELINQUENT
    if (numOutstandingLoan != null && numOutstandingLoan > 2) {
      paymentProfileType = "DELINQUENT";
    }

    return new DebtorWithProfile(new Debtor(
      debtorRecord.getId(),
      debtorRecord.getName(),
      debtorRecord.getEmail(),
      debtorRecord.getDocumentid()
    ), paymentProfileType);
  }

  public List<Debtor> getDebtors() {
    return dslContext.select().from(com.fachri.aproject.generated.tables.Debtor.DEBTOR).fetchInto(Debtor.class);
  }

  public Debtor getDebtor(Long debtorId) {
    return dslContext.select().from(com.fachri.aproject.generated.tables.Debtor.DEBTOR)
      .where(com.fachri.aproject.generated.tables.Debtor.DEBTOR.ID.eq(debtorId))
      .fetchOneInto(Debtor.class);
  }
}
