package eu.tax_rate.app.rest.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.util.StringUtils.hasText;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateTaxRate {
    private String municipalityName;
    private LocalDate validFromDate;
    private LocalDate validToDate;
    private String taxRate;

    public void validate() {
        // Validate municipality name.
        if (!hasText(municipalityName)) {
            final String errorMessage = String.format("Provided municipality name is not valid '%s'", municipalityName);
            throw new IllegalArgumentException(errorMessage);
        }

        // Validate dates.
        if (validFromDate.isAfter(validToDate)) {
            final String errorMessage = String.format("Valid from date '%s' cannot be after valid to date '%s'", validFromDate, validToDate);
            throw new IllegalArgumentException(errorMessage);
        }

        // Validate taxRate.
        new BigDecimal(taxRate);
    }
}
