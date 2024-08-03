package eu.tax_rates.app.rest;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ApplicableTaxRate {
    private final String municipalityName;
    private final LocalDate validForDate;
    private final String applicableTaxRate;
}
