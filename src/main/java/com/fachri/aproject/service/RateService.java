package com.fachri.aproject.service;

import org.apache.commons.lang3.function.TriFunction;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
public class RateService {

  public List<LoanRate> calculateLoanRates(BigDecimal principal, BigDecimal rate, int term, String rateType) {
    if (rateType == null || rateType.isEmpty()) {
      throw new IllegalArgumentException("Rate type must not be null or empty");
    }
    RateType rateTypeEnum = RateType.RATE_TYPE_MAP.get(rateType.toUpperCase());
    if (rateTypeEnum == null) {
      throw new IllegalArgumentException("Unsupported rate type: " + rateType);
    }
    return rateTypeEnum.calculate(principal, rate, term);
  }


  public record LoanRate(BigDecimal principal, BigDecimal interest) {

  }

  public enum RateType {
    FLAT(
        (principal, rate, term) -> {
          BigDecimal monthlyPrincipal = principal.divide(BigDecimal.valueOf(term), RoundingMode.HALF_UP);
          BigDecimal monthlyInterest = principal.multiply(rate).divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP)
              .divide(BigDecimal.valueOf(12), RoundingMode.HALF_UP);

          return java.util.stream.IntStream.rangeClosed(1, term)
              .mapToObj(i -> new LoanRate(monthlyPrincipal, monthlyInterest))
              .toList();
        }
    ),
    EFFECTIVE(
        (principal, rate, term) -> {
          BigDecimal monthlyRate = rate.divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP)
              .divide(BigDecimal.valueOf(12), RoundingMode.HALF_UP);
          BigDecimal denominator = BigDecimal.ONE.subtract(
              BigDecimal.ONE.add(monthlyRate).pow(-term)
          );
          BigDecimal monthlyPayment = principal.multiply(monthlyRate).divide(denominator, RoundingMode.HALF_UP);
          var principalRef = principal.add(BigDecimal.ZERO); // Use array to hold mutable principal value
          List<LoanRate> loanRates = new java.util.ArrayList<>(term);
          for (int i = 0; i < term; i++) {
            BigDecimal monthlyInterest = principalRef.multiply(monthlyRate).setScale(2, RoundingMode.HALF_UP);
            BigDecimal monthlyPrincipal = monthlyPayment.subtract(monthlyInterest).setScale(2, RoundingMode.HALF_UP);
            loanRates.add(new LoanRate(monthlyPrincipal, monthlyInterest));
            principalRef = principalRef.subtract(monthlyPrincipal);
          }
          return loanRates;
        }
    );

    // Map of rate type names to enum instances for easy and fast lookup
    private static final Map<String, RateType> RATE_TYPE_MAP = java.util.Arrays.stream(RateType.values())
        .collect(java.util.stream.Collectors.toMap(Enum::name, Function.identity()));

    TriFunction<BigDecimal, BigDecimal, Integer, List<LoanRate>> calculator;

    RateType(TriFunction<BigDecimal, BigDecimal, Integer, List<LoanRate>> calculator) {
      this.calculator = calculator;
    }

    public List<LoanRate> calculate(BigDecimal principal, BigDecimal rate, int term) {
      return calculator.apply(principal, rate, term);
    }
  }
}
