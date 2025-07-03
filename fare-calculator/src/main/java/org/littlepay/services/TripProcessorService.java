package org.littlepay.services;

import lombok.extern.log4j.Log4j2;
import org.littlepay.enums.TapTypeEnum;
import org.littlepay.enums.TripStatusEnum;
import org.littlepay.model.Tap;
import org.littlepay.model.Trip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service that processes raw tap data and generates corresponding Trip records.
 * Handles matching ON/OFF taps, trip categorization (COMPLETED, CANCELLED, INCOMPLETE),
 * and calculates duration and fare using the FareCalculatorService.
 */
@Service
@Log4j2
public class TripProcessorService {

    @Autowired
    private FareCalculatorService fareCalculatorService;

    /**
     * Processes a list of tap records and converts them into structured trip records.
     *
     * @param tapsData  List of tap entries sorted by time.
     * @return          List of derived Trip objects.
     */
    public List<Trip> processTapsData(List<Tap> tapsData) {
        log.debug("Start: Processing tap data");

        List<Trip> trips = new ArrayList<>();
        Map<String, Tap> activeOnTaps = new HashMap<>();

        Map<String, List<Tap>> tapsByBusIdPan = tapsData.stream()
                .collect(Collectors.groupingBy( tap -> tap.getBusID() + "-" + tap.getPan() ));

        tapsByBusIdPan.forEach((tapKey, taps) -> {
            taps.sort(Comparator.comparing(Tap::getDateTimeUTC));

            for (Tap tap : taps) {
                if (TapTypeEnum.ON.equals(tap.getTapType())) {
                    if (activeOnTaps.containsKey(tapKey)) {
                        Tap prevOnTap = activeOnTaps.remove(tapKey);
                        trips.add(buildIncompleteTrip(prevOnTap));
                    }
                    activeOnTaps.put(tapKey, tap);
                } else if (TapTypeEnum.OFF.equals(tap.getTapType())) {
                    Tap onTap = activeOnTaps.remove(tapKey);
                    if (onTap != null) {
                        trips.add(buildTrip(onTap, tap));
                    } else {
                        log.warn("Tap OFF without a matching ON tap for BUS ID:{} and PAN: {} at {}", tap.getBusID(), tap.getPan(), tap.getDateTimeUTC());
                    }
                }
            }
        });

        for (Tap onTap : activeOnTaps.values()) {
            trips.add(buildIncompleteTrip(onTap));
        }

        log.debug("End: Processing tap data");

        return trips;
    }

    /**
     * Builds a trip object from a matching ON and OFF tap.
     *
     * @param onTap     The tap ON record.
     * @param offTap    The corresponding tap OFF record.
     * @return          A completed or cancelled Trip object.
     */
    private Trip buildTrip(Tap onTap, Tap offTap) {
        log.debug("Start: Building trip");

        TripStatusEnum status;
        BigDecimal chargeAmount;
        Long duration = Duration.between(onTap.getDateTimeUTC(), offTap.getDateTimeUTC()).getSeconds();

        if (onTap.getStopId().equals(offTap.getStopId())) {
            status = TripStatusEnum.CANCELLED;
            chargeAmount = BigDecimal.ZERO;
        } else {
            status = TripStatusEnum.COMPLETED;
            chargeAmount = fareCalculatorService.calculateFare(onTap.getStopId(), offTap.getStopId(), false);
        }

        Trip trip = Trip.builder()
                .started(onTap.getDateTimeUTC())
                .finished(offTap.getDateTimeUTC())
                .durationSecs(duration)
                .fromStopId(onTap.getStopId())
                .toStopId(offTap.getStopId())
                .chargeAmount(chargeAmount)
                .companyId(onTap.getCompanyId())
                .busID(onTap.getBusID())
                .pan(onTap.getPan())
                .status(status)
                .build();

        log.debug("End: Building trip -> {}", trip.toString());

        return trip;
    }

    /**
     * Builds an incomplete trip object for unmatched ON taps.
     *
     * @param onTap     The unmatched tap ON record.
     * @return          An incomplete Trip object.
     */
    private Trip buildIncompleteTrip(Tap onTap) {
        log.debug("Start: Building incomplete trip");

        BigDecimal chargeAmount = fareCalculatorService.calculateFare(onTap.getStopId(), null, true);

        Trip trip = Trip.builder()
                .started(onTap.getDateTimeUTC())
                .finished(null)
                .durationSecs(0L)
                .fromStopId(onTap.getStopId())
                .toStopId(null)
                .chargeAmount(chargeAmount)
                .companyId(onTap.getCompanyId())
                .busID(onTap.getBusID())
                .pan(onTap.getPan())
                .status(TripStatusEnum.INCOMPLETE)
                .build();

        log.debug("End: Building incomplete trip -> {}", trip.toString());

        return trip;
    }

}
