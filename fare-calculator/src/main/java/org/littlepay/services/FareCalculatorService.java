package org.littlepay.services;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Service responsible for determining the fare of a trip between two stops.
 * It handles normal, reverse, and incomplete trip fare calculations.
 */
@Service
public class FareCalculatorService {

    private static final Map<String, BigDecimal> TRIP_PRICES = new HashMap<>();

    static {
        TRIP_PRICES.put("Stop1-Stop2", new BigDecimal("3.25"));
        TRIP_PRICES.put("Stop2-Stop3", new BigDecimal("5.50"));
        TRIP_PRICES.put("Stop1-Stop3", new BigDecimal("7.30"));
    }

    /**
     * Calculates the fare based on from-stop and to-stop.
     *
     * @param fromStop      The stop where the trip started.
     * @param toStop        The stop where the trip ended (null for incomplete trips).
     * @param isIncomplete  Flag indicating whether the trip is incomplete.
     * @return              The fare to charge as a BigDecimal.
     */
    public BigDecimal calculateFare(String fromStop, String toStop, boolean isIncomplete) {
        if (isIncomplete) {
            return getMaxFare(fromStop);
        }

        String key = fromStop + "-" + toStop;
        String reverseKey = toStop + "-" + fromStop;

        return TRIP_PRICES.getOrDefault(key, TRIP_PRICES.getOrDefault(reverseKey, BigDecimal.ZERO));
    }

    /**
     * Returns the maximum fare possible from a given stop.
     * Used for incomplete trips where a tap OFF is missing.
     *
     * @param fromStop  The stop where the passenger tapped ON.
     * @return          The maximum possible fare from that stop.
     */
    private BigDecimal getMaxFare(String fromStop) {
        return TRIP_PRICES.entrySet().stream()
                .filter(entry -> entry.getKey().contains(fromStop))
                .map(Map.Entry::getValue)
                .max(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);
    }
}
