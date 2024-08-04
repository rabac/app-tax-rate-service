package eu.tax_rate.app.rest;

import eu.tax_rate.app.rest.model.ApplicableTaxRate;
import eu.tax_rate.app.rest.model.CreateTaxRate;
import eu.tax_rate.app.service.TaxRateService;
import eu.tax_rate.app.database.model.TaxRateDocument;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

@Log4j2
@RestController
@RequestMapping("/api/v1/tax-rates")
@Tag(name = "API for performing operations with tax rates.")
public class TaxRateRestController {

    /**
     * Service for performing the actions with tax rates.
     */
    private final TaxRateService taxRateServiceImpl;

    public TaxRateRestController(@NonNull final TaxRateService taxRateServiceImpl) {
        this.taxRateServiceImpl = taxRateServiceImpl;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Add a new tax rate for a municipality and validity period.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "204", description = "No tax rate was found for the provided date"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> registerTaxRate(
            @RequestBody @NonNull final CreateTaxRate createTaxRateModel
    ) {
        log.info("Request received to add new tax rate.");
        taxRateServiceImpl.registerTaxRate(createTaxRateModel);
        return ResponseEntity.ok().build();
    }

    /**
     * API to get the applicable tax rate for the provided municipality name and the given date.
     *
     * @param municipalityName to search tax rate for.
     * @param validForDate     to search tax rate for.
     * @return tax rate applicable if found else empty body.
     */
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
            @RequestParam("validForDate") final LocalDate validForDate
    ) {
        log.info(
                "Received request for tax rate with municipality name '{}' and applicable date '{}'",
                municipalityName,
                validForDate
        );

        // TODO input validation and response based on it. Add Advice class for controller.

        final Optional<TaxRateDocument> applicableTax =
                taxRateServiceImpl.getTaxRateDocument(municipalityName, validForDate);
        return applicableTax
                .map(taxRateFound(validForDate))
                .orElseGet(taxRateNotFound(municipalityName, validForDate))
                ;
    }

    private static Function<TaxRateDocument, ResponseEntity<ApplicableTaxRate>> taxRateFound(final LocalDate validForDate) {
        return taxRateDocument -> {
            log.info(
                    "Tax rate is '{}' for municipality '{}' and date '{}'.",
                    taxRateDocument.getTaxRate(),
                    taxRateDocument.getMunicipalityName(),
                    validForDate
            );
            return ResponseEntity.ok(
                    ApplicableTaxRate.builder()
                            .municipalityName(taxRateDocument.getMunicipalityName())
                            .taxRate(taxRateDocument.getTaxRate())
                            .validForDate(validForDate)
                            .build()
            );
        };
    }

    private static Supplier<ResponseEntity<ApplicableTaxRate>> taxRateNotFound(
            final String municipalityName,
            final LocalDate validForDate
    ) {
        return () -> {
            log.info(
                    "No tax rate found for municipality '{}' and date '{}'.",
                    municipalityName,
                    validForDate
            );
            return ResponseEntity.notFound().build();
        };
    }
}