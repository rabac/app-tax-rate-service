package eu.tax_rate.app.service

import eu.tax_rate.app.database.model.TaxRateDocument
import eu.tax_rate.app.rest.model.CreateTaxRate
import org.springframework.data.mongodb.core.MongoTemplate
import spock.lang.Specification

import java.time.LocalDate

class TaxRateServiceSpec extends Specification {

    MongoTemplate mongoTemplate = Mock()
    TaxRateService taxRateService = new TaxRateServiceImpl(mongoTemplate)

    def "verify unsuccessful creation of new tax rate"() {
        given: "not valid tax rate to add"
            CreateTaxRate taxRateToAdd = Mock()
            taxRateToAdd.validate() >> { throw new IllegalArgumentException("The tax rate is not valid") }

        when: "method to add tax rate is invoked"
            taxRateService.registerTaxRate(taxRateToAdd)

        then: "verify exception is thrown"
            thrown IllegalArgumentException

        and: "verify that mongoTemplate is invoked with the same object"
            0 * mongoTemplate.save(taxRateToAdd)
    }

    def "verify successful creation of new tax rate"() {
        given: "tax rate to add"
            CreateTaxRate taxRateToAdd =
                CreateTaxRate.builder()
                    .taxRate("0.1")
                    .municipalityName("Copenhagen")
                    .validFromDate(LocalDate.parse(validFromDate))
                    .validToDate(LocalDate.parse(validToDate))
                    .build()

            TaxRateDocument taxRateDocument =
                TaxRateDocument.builder()
                    .municipalityName("Copenhagen")
                    .validFromDate(taxRateToAdd.getValidFromDate())
                    .validToDate(taxRateToAdd.getValidToDate())
                    .taxRate(taxRateToAdd.getTaxRate())
                    .priority(priority)
                    .build()

        when: "method to add tax rate is invoked"
            taxRateService.registerTaxRate(taxRateToAdd)

        then: "verify that mongoTemplate is invoked with the same object"
            1 * mongoTemplate.save(taxRateDocument)

        where: "various values of dates"
            validFromDate | validToDate  | priority
            "2024-01-01"  | "2024-01-01" | 0
            "2024-01-01"  | "2024-01-02" | 1
            "2024-01-01"  | "2024-01-31" | 30
            "2024-05-01"  | "2024-05-31" | 30
            "2024-01-01"  | "2024-12-31" | 365
    }

    def "verify lookup of correct tax rate"() {
        given:
            List<TaxRateDocument> taxRateDocuments =
                List.of(
                    TaxRateDocument.builder()
                        .municipalityName("Copenhagen")
                        .validFromDate(LocalDate.parse("2024-01-01"))
                        .validToDate(LocalDate.parse("2024-01-01"))
                        .taxRate("0.1")
                        .priority(0)
                        .build(),
                    TaxRateDocument.builder()
                        .municipalityName("Copenhagen")
                        .validFromDate(LocalDate.parse("2024-05-01"))
                        .validToDate(LocalDate.parse("2024-05-31"))
                        .taxRate("0.4")
                        .priority(30)
                        .build(),
                    TaxRateDocument.builder()
                        .municipalityName("Copenhagen")
                        .validFromDate(LocalDate.parse("2024-01-01"))
                        .validToDate(LocalDate.parse("2024-12-31"))
                        .taxRate("0.2")
                        .priority(365)
                        .build(),
                    TaxRateDocument.builder()
                        .municipalityName("Copenhagen")
                        .validFromDate(LocalDate.parse("2024-12-25"))
                        .validToDate(LocalDate.parse("2024-12-25"))
                        .taxRate("0.1")
                        .priority(0)
                        .build(),
                    TaxRateDocument.builder()
                        .municipalityName("Munich")
                        .validFromDate(LocalDate.parse("2024-03-01"))
                        .validToDate(LocalDate.parse("2024-03-30"))
                        .taxRate("0.1")
                        .priority(0)
                        .build()
                )

            1 * mongoTemplate.findAll(_) >> taxRateDocuments

        when: "request for tax rate for given date"
            Optional<TaxRateDocument> foundTaxRate = taxRateService.getTaxRateDocument(municipality, LocalDate.parse(validForDate))

        then: "tax rate was successfully found"
            if (expectedTaxRateFound) {
                assert foundTaxRate.isPresent()
                assert foundTaxRate.get().municipalityName == municipality
                assert foundTaxRate.get().taxRate == expectedTaxRate
            } else {
                assert foundTaxRate.isEmpty()
            }

        where: "various combinations"
            municipality | validForDate || expectedTaxRateFound | expectedTaxRate
            "Copenhagen" | "2024-01-01" || true                 | "0.1"
            "Copenhagen" | "2024-12-25" || true                 | "0.1"
            "Copenhagen" | "2024-03-16" || true                 | "0.2"
            "Copenhagen" | "2024-05-02" || true                 | "0.4"
            "Copenhagen" | "2024-07-10" || true                 | "0.2"
            "Copenhagen" | "2023-01-01" || false                | null
            "Munich"     | "2023-01-01" || false                | null
            "Munich"     | "2024-01-01" || false                | null
            "Amsterdam"  | "2024-01-01" || false                | null
    }
}
