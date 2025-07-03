package org.littlepay.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.littlepay.enums.TripStatusEnum;
import org.littlepay.exceptions.ApplicationException;
import org.littlepay.model.Trip;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TripWriterServiceTest {

    private TripWriterService tripWriterService;
    private Path tempFile;

    @BeforeEach
    void setUp() throws Exception {
        tripWriterService = new TripWriterService();
        tempFile = Files.createTempFile("trips", ".csv");
    }

    @Test
    void testWriteTripsToCSV_validTripList_createsCSVFile() throws Exception {
        Trip trip = getTripMock();

        List<Trip> trips = Collections.singletonList(trip);

        // Act
        tripWriterService.writeTripsToCSV(tempFile.toString(), trips);

        // Assert file exists and content
        assertTrue(Files.exists(tempFile));
        List<String> lines = Files.readAllLines(tempFile);
        assertEquals(2, lines.size()); // 1 header + 1 data line

        String header = lines.get(0);
        assertTrue(header.contains("Started"));
        assertTrue(lines.get(1).contains("Stop1"));
        assertTrue(lines.get(1).contains("5500005555555559"));
    }

    private static Trip getTripMock() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return Trip.builder()
                .started(LocalDateTime.parse("22-01-2023 13:00:00", formatter))
                .finished(LocalDateTime.parse("22-01-2023 13:05:00", formatter))
                .durationSecs(300L)
                .fromStopId("Stop1")
                .toStopId("Stop2")
                .chargeAmount(new BigDecimal("3.25"))
                .companyId("Company1")
                .busID("Bus37")
                .pan("5500005555555559")
                .status(TripStatusEnum.COMPLETED)
                .build();
    }

    @Test
    void testWriteTripsToCSV_invalidPath_throwsApplicationException() {
        List<Trip> trips = Collections.emptyList();

        String invalidPath = "/invalid/directory/trips.csv";
        ApplicationException ex = assertThrows(
                ApplicationException.class,
                () -> tripWriterService.writeTripsToCSV(invalidPath, trips)
        );

        assertTrue(ex.getMessage().contains("Error writing CSV file"));
    }
}
