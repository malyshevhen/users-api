package com.github.malyshevhen.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class DateRangeTest {

    @ParameterizedTest
    @MethodSource("provideValidDateRanges")
    void shouldCreateValidDateRange(LocalDate from, LocalDate to) {
        DateRange dateRange = new DateRange(from, to);
        assertNotNull(dateRange);
        assertEquals(from, dateRange.getFrom());
        assertEquals(to, dateRange.getTo());
    }

    private static Stream<Arguments> provideValidDateRanges() {
        return Stream.of(
                Arguments.of(LocalDate.of(2023, 1, 1), LocalDate.of(2023, 1, 31)),
                Arguments.of(LocalDate.of(2022, 12, 1), LocalDate.of(2022, 12, 31)),
                Arguments.of(LocalDate.of(2023, 1, 1), LocalDate.now()));
    }

    @Test
    void shouldThrowExceptionWhenFromDateIsAfterToDate() {
        LocalDate from = LocalDate.of(2023, 1, 31);
        LocalDate to = LocalDate.of(2023, 1, 1);
        assertThrows(IllegalArgumentException.class, () -> new DateRange(from, to));
    }

}
