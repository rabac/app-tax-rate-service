package eu.tax_rate.app.service;

import eu.tax_rate.app.database.model.TaxRateDocument;
import eu.tax_rate.app.rest.model.CreateTaxRate;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.springframework.util.StringUtils.hasText;

@Log4j2
@Service
public class TaxRateServiceImpl implements TaxRateService {

    /**
     * Mongo template to use when communicating with mongo.
     */
    private final MongoTemplate mongoTemplate;

    /**
     * Construct instance.
     *
     * @param mongoTemplate to use for communication with mongo.
     */
    public TaxRateServiceImpl(@NonNull final MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void registerTaxRate(@NonNull final CreateTaxRate taxRateToAdd) {
        final boolean validTaxRate = isValid(taxRateToAdd);
        if (!validTaxRate) {
            throw new IllegalArgumentException(
                    String.format("Provided tax rate '%s' is not valid and cannot be added.", taxRateToAdd)
            );
        }

        final int daysInBetween = taxRateToAdd.getValidFromDate().until(taxRateToAdd.getValidToDate()).getDays();
        final TaxRateDocument taxRateDocument =
                TaxRateDocument.builder()
                        .municipalityName(taxRateToAdd.getMunicipalityName())
                        .validFromDate(taxRateToAdd.getValidFromDate())
                        .validToDate(taxRateToAdd.getValidToDate())
                        .taxRate(taxRateToAdd.getTaxRate())
                        .priority(daysInBetween)
                        .build();
        final TaxRateDocument savedDocument = this.mongoTemplate.save(taxRateDocument);
        log.info("Saved new tax rate '{}'", savedDocument);
    }

    @Override
    public Optional<TaxRateDocument> getTaxRateDocument(
            @NonNull final String municipalityName,
            @NonNull final LocalDate validForDate
    ) {
        if (!hasText(municipalityName)) {
            log.warn("Cannot find the tax rate for blank municipality name.");
            return Optional.empty();
        }

        // TODO: For Phase 2 - can be improved by using filtering and sorting in mongo itself.
        final List<TaxRateDocument> taxRateDocuments = mongoTemplate.findAll(TaxRateDocument.class);
        return
                taxRateDocuments.stream()
                        .filter(document -> document.getMunicipalityName().equals(municipalityName))
                        .filter(document ->
                                isEqualOrAfter(validForDate, document.getValidFromDate()) &&
                                        isEqualOrBefore(validForDate, document.getValidToDate())
                        ).min(Comparator.comparingInt(TaxRateDocument::getPriority))
                ;
    }

    private boolean isValid(final CreateTaxRate taxRateToAdd) {
        // TODO add validity conditions.
        return true;
    }

    private static boolean isEqualOrAfter(LocalDate validForDate, LocalDate compareWithDate) {
        return validForDate.isEqual(compareWithDate) || validForDate.isAfter(compareWithDate);
    }

    private static boolean isEqualOrBefore(LocalDate validForDate, LocalDate compareWithDate) {
        return validForDate.isEqual(compareWithDate) || validForDate.isBefore(compareWithDate);
    }
}
