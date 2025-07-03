package org.littlepay.services;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.littlepay.exceptions.ApplicationException;
import org.littlepay.model.Tap;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

@Service
public class TapReaderService {

    public List<Tap> readTapsFromCSV(String filePath) throws ApplicationException {
        try (Reader reader = Files.newBufferedReader(Paths.get(filePath))) {
            CsvToBean<Tap> csvToBean = new CsvToBeanBuilder<Tap>(reader)
                    .withType(Tap.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();
            return csvToBean.parse();
        } catch (IOException e) {
            throw new ApplicationException("Error reading CSV file: " + e.getMessage());
        }
    }
}
