package eu.tax_rate.app.service;

import eu.tax_rate.app.database.model.TaxRateDocument;
import eu.tax_rate.app.rest.model.CreateTaxRate;

import java.time.LocalDate;
import java.util.Optional;

public interface TaxRateService {

    /**
     * Get the tax rate for the provided municipality and date.
     *
     * @param municipalityName municipality for which the tax rate should be valid.
     * @param validForDate     date for which the tax rate should be valid.
     * @return tax rate applied for the given parameters.
     */
    Optional<TaxRateDocument> getTaxRateDocument(
            final String municipalityName,
            final LocalDate validForDate
    );

    /**
     * Create a new tax rate in the database.
     * @param taxRate new tax rate to be created.
     */
    void registerTaxRate(final CreateTaxRate taxRate);
}
