package org.littlepay.services;

import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.littlepay.exceptions.ApplicationException;
import org.littlepay.model.Trip;
import org.springframework.stereotype.Service;

import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.littlepay.constants.CSVFileConstant.*;

@Service
public class TripWriterService {


    public void writeTripsToCSV(String filePath, List<Trip> trips) throws ApplicationException {
        try (Writer writer = Files.newBufferedWriter(Paths.get(filePath))) {

            String[] header = {
                    STARTED_COLUMN_NAME, FINISHED_COLUMN_NAME, DURATION_SECS_COLUMN_NAME, FROM_STOP_ID_COLUMN_NAME, TO_STOP_ID_COLUMN_NAME,
                    CHARGE_AMOUNT_COLUMN_NAME, COMPANY_ID_COLUMN_NAME, BUS_ID_COLUMN_NAME, PAN_COLUMN_NAME, STATUS_COLUMN_NAME
            };

            // 2. Write the header row to the writer
            writer.write(String.join(",", header));
            writer.write("\n");

            ColumnPositionMappingStrategy<Trip> strategy = getTripColumnPositionMappingStrategy();

            StatefulBeanToCsv<Trip> beanToCsv = new StatefulBeanToCsvBuilder<Trip>(writer)
                    .withSeparator(SEPARATOR)
                    .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                    .withEscapechar(ESCAPE_CHAR)
                    .withMappingStrategy(strategy)
                    .build();

            beanToCsv.write(trips);
        } catch(CsvRequiredFieldEmptyException | CsvDataTypeMismatchException e){
            throw new ApplicationException("Error mapping bean to CSV: " + e.getMessage());
        } catch(Exception e){
            throw new ApplicationException("Error writing CSV file: " + e.getMessage());
        }
    }

    private static ColumnPositionMappingStrategy<Trip> getTripColumnPositionMappingStrategy() {

        String[] columnsMapping = {
                "started", "finished", "durationSecs", "fromStopId", "toStopId",
                "chargeAmount", "companyId", "busID", "pan", "status"
        };

        ColumnPositionMappingStrategy<Trip> strategy = new ColumnPositionMappingStrategy<>();
        strategy.setType(Trip.class);
        strategy.setColumnMapping(columnsMapping);
        return strategy;
    }
}
