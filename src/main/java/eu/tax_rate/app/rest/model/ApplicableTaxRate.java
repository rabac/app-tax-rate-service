package eu.tax_rate.app.rest.model;

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
