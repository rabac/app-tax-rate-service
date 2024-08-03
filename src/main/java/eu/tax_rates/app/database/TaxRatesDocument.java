package eu.tax_rates.app.database;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class TaxRatesDocument {
    private final String municipalityName;
    private final LocalDate validFromDate;
    private final LocalDate validToDate;
    private final String taxRate;
    private final int priority;
}
