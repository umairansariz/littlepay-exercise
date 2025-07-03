package org.littlepay.model;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.littlepay.enums.TapTypeEnum;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tap {
    @CsvBindByName(column = "ID")
    private Long id;

    @CsvDate(value = "dd-MM-yyyy HH:mm:ss")
    @CsvBindByName(column = "DateTimeUTC")
    private LocalDateTime dateTimeUTC;

    @CsvBindByName(column = "TapType")
    private TapTypeEnum tapType; // ON or OFF

    @CsvBindByName(column = "StopId")
    private String stopId;

    @CsvBindByName(column = "CompanyId")
    private String companyId;

    @CsvBindByName(column = "BusID")
    private String busID;

    @CsvBindByName(column = "PAN")
    private String pan;
}