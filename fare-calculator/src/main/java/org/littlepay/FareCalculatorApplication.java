package org.littlepay;

import lombok.extern.log4j.Log4j2;
import org.littlepay.exceptions.ApplicationException;
import org.littlepay.model.Tap;
import org.littlepay.model.Trip;
import org.littlepay.services.TapReaderService;
import org.littlepay.services.TripProcessorService;
import org.littlepay.services.TripWriterService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
@Log4j2
public class FareCalculatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(FareCalculatorApplication.class, args);
    }

    @Bean
    public CommandLineRunner run(TapReaderService tapReaderService, TripProcessorService tripProcessorService, TripWriterService tripWriterService) {
        return args -> {
            String inputFilePath = "taps.csv"; // Input file
            String outputFilePath = "trips.csv"; // Output file
            try {
                log.info("Processing taps from: {}", inputFilePath);

                List<Tap> taps = tapReaderService.readTapsFromCSV(inputFilePath);
                List<Trip> trips = tripProcessorService.processTapsData(taps);
                tripWriterService.writeTripsToCSV(outputFilePath, trips);

                log.info("Added calculated trips in: {}", outputFilePath);
            } catch(ApplicationException e){
                log.error("Error in Application -> detail: {}", e.getMessage());
            }
        };
    }

}
