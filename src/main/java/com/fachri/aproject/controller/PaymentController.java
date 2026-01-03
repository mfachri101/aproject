package com.fachri.aproject.controller;

import com.fachri.aproject.service.PaymentService;
import com.fachri.aproject.service.model.PaymentRequest;
import com.fachri.aproject.service.model.PaymentResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
public class PaymentController {
  @Autowired
  PaymentService paymentService;

  @Operation(
    summary = "Make a payment",
    description = "Processes a payment request and returns the result."
  )
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Payment processed successfully",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = PaymentResult.class)))
  })
  @PostMapping(value = "/payment/pay/{asOfDate}", consumes = "application/json", produces = "application/json")
  public PaymentResult makePayment(
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
      description = "Payment request details",
      required = true,
      content = @Content(schema = @Schema(implementation = PaymentRequest.class))
    )
    @org.springframework.web.bind.annotation.RequestBody PaymentRequest paymentRequest, @PathVariable("asOfDate") LocalDate asOfDate) {
    return paymentService.makePayment(paymentRequest, asOfDate);
  }
}
