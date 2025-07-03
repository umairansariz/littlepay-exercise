package org.littlepay.util;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeConverter extends AbstractBeanField<LocalDateTime, String> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    // This method is for READING from CSV.
    @Override
    protected Object convert(String value) throws CsvDataTypeMismatchException {
        if (StringUtils.isEmpty(value)) {
            return value;
        }
        try {
            return LocalDateTime.parse(value.trim(), FORMATTER);
        } catch (NumberFormatException e) {
            throw new CsvDataTypeMismatchException("DateTimeFormatter convert: Cannot parse date-time " + value + " with format 'dd-MM-yyyy HH:mm:ss': " + e.getMessage());
        }
    }

    // This method is for WRITING to CSV.
    @Override
    public String convertToWrite(Object value) throws CsvDataTypeMismatchException {
        if (value == null) {
            return "";
        }

        if (value instanceof LocalDateTime) {
            return ((LocalDateTime) value).format(FORMATTER);
        } else {
            throw new CsvDataTypeMismatchException("DateTimeFormatter convertToWrite: Expected LocalDateTime for date field, but got: " + value.getClass().getName());
        }
    }
}
