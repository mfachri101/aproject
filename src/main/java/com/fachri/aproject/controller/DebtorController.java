package com.fachri.aproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import com.fachri.aproject.service.DebtorProfileRequest;
import com.fachri.aproject.service.DebtorService;
import com.fachri.aproject.service.model.Debtor;
import com.fachri.aproject.service.repository.DebtorWithProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
public class DebtorController {
  @Autowired
  DebtorService debtorService;

  @Operation(summary = "Get all debtors", description = "Returns a list of all registered debtors.")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "List of debtors",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = Debtor.class)))
  })
  @GetMapping(value = "/debtor", produces = "application/json")
  public List<Debtor> getDebtors() {
    return debtorService.getDebtors();
  }

  @Operation(summary = "Register a new debtor", description = "Registers a new debtor and returns the created debtor object.")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Debtor registered",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = Debtor.class)))
  })
  @PutMapping(value = "/debtor/register", consumes = "application/json", produces = "application/json")
  public Debtor register(
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
      description = "Debtor object to register",
      required = true,
      content = @Content(schema = @Schema(implementation = Debtor.class))
    )
    @RequestBody Debtor debtor) {
    return debtorService.registerBorrower(debtor);
  }

  @Operation(summary = "Get debtor profile", description = "Returns the profile of a debtor as of a specific date.")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Debtor profile",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = DebtorWithProfile.class)))
  })
  @GetMapping(value = "/debtor/{debtorId}/profile/{asOfDate}", produces = "application/json")
  public DebtorWithProfile getDebtorProfile(
    @Parameter(description = "ID of the debtor", required = true) @PathVariable("debtorId") Long debtorId,
    @Parameter(description = "Profile as of date (yyyy-MM-dd), serves for the testing purpose to check past and upcoming date.", required = true) @PathVariable("asOfDate") LocalDate asOfDate) {
    return debtorService.getDebtorProfile(new DebtorProfileRequest(debtorId, asOfDate));
  }
}
