package org.littlepay.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.littlepay.enums.TapTypeEnum;
import org.littlepay.enums.TripStatusEnum;
import org.littlepay.model.Tap;
import org.littlepay.model.Trip;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TripProcessorServiceTest {

    @Mock
    private FareCalculatorService fareCalculatorService;

    @InjectMocks
    private TripProcessorService tripProcessorService;

    private Tap tapOn;
    private Tap tapOffSameStop;
    private Tap tapOffDifferentStop;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);

        tapOn = getTapOn();
        tapOffSameStop = getTapOffSameStop();
        tapOffDifferentStop = getTapOffDifferentStop();
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void testCompletedTrip_createsCompletedTrip() {
        when(fareCalculatorService.calculateFare("Stop1", "Stop2", false))
                .thenReturn(new BigDecimal("3.25"));

        List<Tap> taps = List.of(tapOn, tapOffDifferentStop);
        List<Trip> trips = tripProcessorService.processTapsData(taps);

        assertEquals(1, trips.size());
        Trip trip = trips.getFirst();

        assertEquals(TripStatusEnum.COMPLETED, trip.getStatus());
        assertEquals("Stop1", trip.getFromStopId());
        assertEquals("Stop2", trip.getToStopId());
        assertEquals(new BigDecimal("3.25"), trip.getChargeAmount());

        verify(fareCalculatorService).calculateFare("Stop1", "Stop2", false);
    }

    @Test
    void testCancelledTrip_createsCancelledTrip() {
        List<Tap> taps = List.of(tapOn, tapOffSameStop);
        List<Trip> trips = tripProcessorService.processTapsData(taps);

        assertEquals(1, trips.size());
        Trip trip = trips.getFirst();

        assertEquals(TripStatusEnum.CANCELLED, trip.getStatus());
        assertEquals(BigDecimal.ZERO, trip.getChargeAmount());

        verify(fareCalculatorService, never()).calculateFare(any(), any(), anyBoolean());
    }

    @Test
    void testIncompleteTrip_createsIncompleteTrip() {
        when(fareCalculatorService.calculateFare("Stop1", null, true))
                .thenReturn(new BigDecimal("7.30"));

        List<Tap> taps = List.of(tapOn); // No OFF
        List<Trip> trips = tripProcessorService.processTapsData(taps);

        assertEquals(1, trips.size());
        Trip trip = trips.getFirst();

        assertEquals(TripStatusEnum.INCOMPLETE, trip.getStatus());
        assertNull(trip.getToStopId());
        assertEquals(new BigDecimal("7.30"), trip.getChargeAmount());

        verify(fareCalculatorService).calculateFare("Stop1", null, true);
    }

    private static Tap getTapOffDifferentStop() {
        return Tap.builder()
                .id(3L)
                .dateTimeUTC(LocalDateTime.of(2023, 1, 22, 13, 10, 0))
                .tapType(TapTypeEnum.OFF)
                .stopId("Stop2") // different stop = COMPLETED
                .companyId("Company1")
                .busID("Bus1")
                .pan("5500000000000004")
                .build();
    }

    private static Tap getTapOffSameStop() {
        return Tap.builder()
                .id(2L)
                .dateTimeUTC(LocalDateTime.of(2023, 1, 22, 13, 5, 0))
                .tapType(TapTypeEnum.OFF)
                .stopId("Stop1") // same stop = CANCELLED
                .companyId("Company1")
                .busID("Bus1")
                .pan("5500000000000004")
                .build();
    }

    private static Tap getTapOn() {
        return Tap.builder()
                .id(1L)
                .dateTimeUTC(LocalDateTime.of(2023, 1, 22, 13, 0, 0))
                .tapType(TapTypeEnum.ON)
                .stopId("Stop1")
                .companyId("Company1")
                .busID("Bus1")
                .pan("5500000000000004")
                .build();
    }
}
