package org.littlepay.model;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.littlepay.enums.TripStatusEnum;
import org.littlepay.util.BigDecimalConverter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Trip {
    @CsvBindByName(column = "Started")
    private LocalDateTime started;

    @CsvBindByName(column = "Finished")
    private LocalDateTime finished;

    @CsvBindByName(column = "DurationSecs")
    private Long durationSecs;

    @CsvBindByName(column = "FromStopId")
    private String fromStopId;

    @CsvBindByName(column = "ToStopId")
    private String toStopId;

    @CsvCustomBindByName(column = "ChargeAmount", converter = BigDecimalConverter.class)
    private BigDecimal chargeAmount;

    @CsvBindByName(column = "CompanyId")
    private String companyId;

    @CsvBindByName(column = "BusID")
    private String busID;

    @CsvBindByName(column = "PAN")
    private String pan;

    @CsvBindByName(column = "Status")
    private TripStatusEnum status;
}
