package eu.tax_rates.app.service;

import eu.tax_rates.app.database.TaxRatesDocument;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
public class TaxRatesService {

    private final List<TaxRatesDocument> taxRatesDocuments;

    public TaxRatesService() {
        taxRatesDocuments = new LinkedList<>();
        taxRatesDocuments.add(
                TaxRatesDocument.builder()
                        .municipalityName("Copenhagen")
                        .validFromDate(LocalDate.parse("2024-01-01"))
                        .validToDate(LocalDate.parse("2024-12-30"))
                        .taxRate("0.2")
                        .priority(364)
                        .build()
        );
        taxRatesDocuments.add(
                TaxRatesDocument.builder()
                        .municipalityName("Copenhagen")
                        .validFromDate(LocalDate.parse("2024-05-01"))
                        .validToDate(LocalDate.parse("2024-05-31"))
                        .taxRate("0.4")
                        .priority(30)
                        .build()
        );
        taxRatesDocuments.add(
                TaxRatesDocument.builder()
                        .municipalityName("Copenhagen")
                        .validFromDate(LocalDate.parse("2024-01-01"))
                        .validToDate(LocalDate.parse("2024-01-01"))
                        .taxRate("0.1")
                        .priority(0)
                        .build()
        );
        taxRatesDocuments.add(
                TaxRatesDocument.builder()
                        .municipalityName("Copenhagen")
                        .validFromDate(LocalDate.parse("2024-12-25"))
                        .validToDate(LocalDate.parse("2024-12-25"))
                        .taxRate("0.1")
                        .priority(0)
                        .build()
        );
    }

    public Optional<TaxRatesDocument> getTaxRatesDocument(
            @NonNull final String municipalityName,
            @NonNull final LocalDate validForDate
    ) {
        if (!StringUtils.hasText(municipalityName)) {
            return Optional.empty();
        }

        return
                taxRatesDocuments.stream()
                        .filter(document -> document.getMunicipalityName().equals(municipalityName))
                        .filter(document ->
                                isEqualOrAfter(validForDate, document.getValidFromDate()) &&
                                        isEqualOrBefore(validForDate, document.getValidToDate())
                        ).min(Comparator.comparingInt(TaxRatesDocument::getPriority))
                ;
    }

    private static boolean isEqualOrAfter(LocalDate validForDate, LocalDate compareWithDate) {
        return validForDate.isEqual(compareWithDate) || validForDate.isAfter(compareWithDate);
    }

    private static boolean isEqualOrBefore(LocalDate validForDate, LocalDate compareWithDate) {
        return validForDate.isEqual(compareWithDate) || validForDate.isBefore(compareWithDate);
    }
}
