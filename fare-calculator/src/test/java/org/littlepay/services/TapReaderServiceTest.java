package org.littlepay.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.littlepay.exceptions.ApplicationException;
import org.littlepay.model.Tap;

import java.net.URL;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TapReaderServiceTest {

    private TapReaderService tapReaderService;
    private String validFilePath;

    @BeforeEach
    void setUp() throws Exception {
        tapReaderService = new TapReaderService();

        URL resource = getClass().getClassLoader().getResource("taps-test.csv");
        assertNotNull(resource, "Test file not found in resources.");
        validFilePath = Paths.get(resource.toURI()).toString();
    }

    @Test
    void testReadTapsFromCSV_validFile_returnsParsedList() throws ApplicationException {
        List<Tap> taps = tapReaderService.readTapsFromCSV(validFilePath);

        assertNotNull(taps);
        assertEquals(2, taps.size());

        Tap tap = taps.getFirst();
        assertEquals("Stop1", tap.getStopId());
        assertEquals("ON", tap.getTapType());
    }

    @Test
    void testReadTapsFromCSV_invalidFile_throwsApplicationException() {
        String invalidPath = "src/test/resources/missing.csv";
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> tapReaderService.readTapsFromCSV(invalidPath)
        );

        assertTrue(exception.getMessage().contains("Error reading CSV file"));
    }
}