package com.github.malyshevhen.models;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;

public record DateRange(
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

    public DateRange {
        if (from != null && to != null && from.isAfter(to)) {
            throw new IllegalArgumentException("From date must be before to date");
        }
    }

    @Valid
    @Past
    @JsonProperty("from")
    public LocalDate getFrom() {
        return from;
    }

    @Valid
    @PastOrPresent
    @JsonProperty("to")
    public LocalDate getTo() {
        return to;
    }

    public boolean isSet() {
        return from != null || to != null;
    }
}
