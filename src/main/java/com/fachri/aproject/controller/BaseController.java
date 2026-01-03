package com.fachri.aproject.controller;

import org.springframework.http.ResponseEntity;

import java.util.function.Supplier;

public class BaseController {
  public <T> ResponseEntity<T> buildResponse(T body, Supplier<String> operationResult) {
    // convert operation result to http status if needed
    return ResponseEntity.ok(body);
  }
}
