package eu.tax_rate.app.rest.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateTaxRate {
    private String municipalityName;
    private LocalDate validFromDate;
    private LocalDate validToDate;
    private String taxRate;
}
