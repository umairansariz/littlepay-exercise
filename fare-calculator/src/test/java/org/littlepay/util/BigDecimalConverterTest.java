package org.littlepay.util;

import com.opencsv.exceptions.CsvDataTypeMismatchException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class BigDecimalConverterTest {

    private BigDecimalConverter converter;

    @BeforeEach
    void setUp() {
        converter = new BigDecimalConverter();
    }

    @Test
    void testConvert_validDollarString() throws CsvDataTypeMismatchException {
        Object result = converter.convert("$3.25");
        assertInstanceOf(BigDecimal.class, result);
        assertEquals(new BigDecimal("3.25"), result);
    }

    @Test
    void testConvert_validUnformattedString() throws CsvDataTypeMismatchException {
        Object result = converter.convert("7.30");
        assertEquals(new BigDecimal("7.30"), result);
    }

    @Test
    void testConvert_valueWithSpaces() throws CsvDataTypeMismatchException {
        Object result = converter.convert("   $5.50  ");
        assertEquals(new BigDecimal("5.50"), result);
    }

    @Test
    void testConvert_emptyString_throwsException() {
        CsvDataTypeMismatchException exception = assertThrows(
                CsvDataTypeMismatchException.class,
                () -> converter.convert("")
        );
        assertTrue(exception.getMessage().contains("Null value is not allowed"));
    }

    @Test
    void testConvert_invalidString_throwsException() {
        CsvDataTypeMismatchException exception = assertThrows(
                CsvDataTypeMismatchException.class,
                () -> converter.convert("abc")
        );
        assertTrue(exception.getMessage().contains("Cannot convert"));
    }

    @Test
    void testConvertToWrite_validBigDecimal() throws CsvDataTypeMismatchException {
        String result = converter.convertToWrite(new BigDecimal("3.25"));
        assertEquals("$3.25", result);
    }

    @Test
    void testConvertToWrite_invalidType_throwsException() {
        CsvDataTypeMismatchException exception = assertThrows(
                CsvDataTypeMismatchException.class,
                () -> converter.convertToWrite("3.25") // Not BigDecimal
        );
        assertTrue(exception.getMessage().contains("Expected BigDecimal"));
    }
}
