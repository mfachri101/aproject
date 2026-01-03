package com.fachri.aproject.controller;

import com.fachri.aproject.service.LoanService;
import com.fachri.aproject.service.model.LoanCreationRequest;
import com.fachri.aproject.service.model.LoanCreationResult;
import com.fachri.aproject.service.model.OutstandingLoanInquiryRequest;
import com.fachri.aproject.service.model.OutstandingLoanInquiryResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
public class LoanController {
  @Autowired
  LoanService loanService;

  @Operation(
    summary = "Process or create a new loans",
    description = "Creates a new loan based on the provided request."
  )
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Loan processed successfully",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoanCreationResult.class)))
  })
  @PutMapping(value = "/loans/process-loans", consumes = "application/json", produces = "application/json")
  public LoanCreationResult processLoans(
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
      description = "Loan creation request, the rate is annual percentage rate, if it's 10% then put 0.1",
      required = true,
      content = @Content(schema = @Schema(implementation = LoanCreationRequest.class))
    )
    @org.springframework.web.bind.annotation.RequestBody LoanCreationRequest loanCreationRequest) {
    return loanService.createLoan(loanCreationRequest);
  }

  @Operation(
    summary = "Get outstanding loan as of date",
    description = "Retrieves the outstanding loan for a debtor as of a specific date."
  )
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Outstanding loan details",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = OutstandingLoanInquiryResult.class)))
  })
  @GetMapping(value = "/loans/outstanding/{debtorId}/{asOfDate}", produces = "application/json")
  public OutstandingLoanInquiryResult getOutstandingLoanAsOfDate(
    @Parameter(description = "ID of the debtor", required = true)
    @PathVariable("debtorId") Long debtorId,
    @Parameter(description = "As of date (yyyy-MM-dd)", required = true)
    @PathVariable(value = "asOfDate") LocalDate asOfDate) {
    return loanService.getOutstandingLoan(new OutstandingLoanInquiryRequest(debtorId, asOfDate));
  }

}
