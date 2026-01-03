package com.fachri.aproject.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class FrequencyService {

  public List<LocalDate> generatePaymentDates(LocalDate startDate, int numberOfPayments, String frequency) {
    if (frequency == null || frequency.isEmpty()) {
      throw new IllegalArgumentException("Frequency must not be null or empty");
    }
    Frequency freq = Frequency.FREQUENCY_MAP.get(frequency.toUpperCase());
    if (freq == null) {
      throw new IllegalArgumentException("Unsupported frequency: " + frequency);
    }
    return freq.generateDates(startDate, numberOfPayments);
  }

  // Enum representing different payment frequencies
  // Each frequency has a method to calculate the next payment date
  public enum Frequency {
    DAILY((startDate, i) -> startDate.plusDays(i + 1)),
    WEEKLY((startDate, i) -> startDate.plusWeeks(i + 1)),
    FORTNIGHTLY((startDate, i) -> startDate.plusWeeks(i + 2)),
    MONTHLY((startDate, i) -> startDate.plusMonths(i + 1)),
    YEARLY((startDate, i) -> startDate.plusYears(i + 1));

    // Map of frequency names to enum instances for easy and fast look
    public static final Map<String, Frequency> FREQUENCY_MAP = Arrays.stream(Frequency.values())
      .collect(Collectors.toMap(Enum::name, Function.identity()));

    private final BiFunction<LocalDate, Integer, LocalDate> dateAdder;

    Frequency(BiFunction<LocalDate, Integer, LocalDate> dateAdder) {
      this.dateAdder = dateAdder;
    }

    public List<LocalDate> generateDates(LocalDate startDate, int numberOfPayments) {
      ArrayList<LocalDate> dates = new ArrayList<>(numberOfPayments);
      for (int i = 0; i < numberOfPayments; i++) {
        dates.add(dateAdder.apply(startDate, i));
      }
      return dates;
    }
  }

}
