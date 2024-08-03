package eu.tax_rates.app.rest;

import eu.tax_rates.app.database.TaxRatesDocument;
import eu.tax_rates.app.service.TaxRatesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Optional;

@Log4j2
@RestController
@RequestMapping("/api/v1/tax-rates")
@Tag(name = "API for performing operations with tax rates.")
public class TaxRatesRestController {

    /**
     * Service for performing the actions with tax rates.
     */
    private final TaxRatesService taxRatesService;

    public TaxRatesRestController(@NonNull final TaxRatesService taxRatesService) {
        this.taxRatesService = taxRatesService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Find the current applicable tax rate.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "204", description = "No tax rate was found for the provided date"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApplicableTaxRate> getApplicableTaxRate(
            @RequestParam("municipalityName") final String municipalityName,
            @RequestParam("applicableDate") final LocalDate applicableDate
    ) {
        log.info("Received request for tax rate with municipality name '{}' and applicable date '{}'", municipalityName, applicableDate);

        final Optional<TaxRatesDocument> applicableTax = taxRatesService.getTaxRatesDocument(municipalityName, applicableDate);
        return applicableTax.map(
                taxRatesDocument -> ResponseEntity.ok(
                        ApplicableTaxRate.builder()
                                .municipalityName(taxRatesDocument.getMunicipalityName())
                                .applicableTaxRate(taxRatesDocument.getTaxRate())
                                .validForDate(applicableDate)
                                .build()
                )).orElseGet(() -> ResponseEntity.notFound().build());
    }
}