package org.littlepay.util;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class BigDecimalConverter extends AbstractBeanField<BigDecimal, String> {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("$0.00");

    // This method is for READING from CSV.
    @Override
    protected Object convert(String value) throws CsvDataTypeMismatchException {
        if (StringUtils.isEmpty(value)) {
            throw new CsvDataTypeMismatchException("BigDecimalConverter convert: Null value is not allowed");
        }
        try {
            // Remove the '$' sign before parsing
            String cleanedValue = value.replace("$", "").trim();
            return new BigDecimal(cleanedValue);
        } catch (NumberFormatException e) {
            throw new CsvDataTypeMismatchException("BigDecimalConverter convert: Cannot convert " + value + " to BigDecimal: " + e.getMessage());
        }
    }

    // This method is for WRITING to CSV.
    @Override
    public String convertToWrite(Object value) throws CsvDataTypeMismatchException {
        if (value instanceof BigDecimal) {
            return DECIMAL_FORMAT.format(value);
        } else {
            throw new CsvDataTypeMismatchException("BigDecimalConverter convertToWrite: Expected BigDecimal for ChargeAmount, but got: " + value.getClass().getName());
        }
    }
}
