package eu.tax_rate.app.database.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor // required by Lombok when using the Builder annotation.
@NoArgsConstructor // required by Mongo for converting bson to java model.
@FieldDefaults(level = AccessLevel.PRIVATE)
@FieldNameConstants
@Document(collection = "taxRate")
public class TaxRateDocument {
    /** Name of the municipality */
    String municipalityName;

    /** Date the tax rate is valid from */
    LocalDate validFromDate;

    /** Date the tax rate is valid until */
    LocalDate validToDate;

    /** Tax rate that applies */
    String taxRate;

    /** Priority of this tax rate configuration, to be used when multiple tax rates are found */
    long priority;
}
