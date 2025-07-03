package org.littlepay.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class FareCalculatorServiceTest {

    private FareCalculatorService fareCalculatorService;

    @BeforeEach
    void setUp() {
        fareCalculatorService = new FareCalculatorService();
    }

    @Test
    void testFareCalculate_completeTrip_forwardDirection() {
        BigDecimal fare = fareCalculatorService.calculateFare("Stop1", "Stop2", false);
        assertEquals(new BigDecimal("3.25"), fare);
    }

    @Test
    void testFareCalculate_completeTrip_reverseDirection() {
        BigDecimal fare = fareCalculatorService.calculateFare("Stop2", "Stop1", false);
        assertEquals(new BigDecimal("3.25"), fare);
    }

    @Test
    void testFareCalculate_completeTrip_Stop2ToStop3() {
        BigDecimal fare = fareCalculatorService.calculateFare("Stop2", "Stop3", false);
        assertEquals(new BigDecimal("5.50"), fare);
    }

    @Test
    void testFareCalculate_incompleteTrip_fromStop2() {
        BigDecimal fare = fareCalculatorService.calculateFare("Stop2", null, true);
        assertEquals(new BigDecimal("5.50"), fare); // Max of Stop2-Stop1 (3.25), Stop2-Stop3 (5.50)
    }

    @Test
    void testFareCalculate_incompleteTrip_fromStop1() {
        BigDecimal fare = fareCalculatorService.calculateFare("Stop1", null, true);
        assertEquals(new BigDecimal("7.30"), fare); // Max of Stop1-Stop2 and Stop1-Stop3
    }

    @Test
    void testFareCalculate_incompleteTrip_noMatchingFare() {
        BigDecimal fare = fareCalculatorService.calculateFare("Unknown", null, true);
        assertEquals(BigDecimal.ZERO, fare);
    }

    @Test
    void testFareCalculate_completeTrip_noMatch() {
        BigDecimal fare = fareCalculatorService.calculateFare("UnknownStopX", "UnknownStopY", false);
        assertEquals(BigDecimal.ZERO, fare);
    }
}
