package eu.tax_rate.app

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import eu.tax_rate.app.database.model.TaxRateDocument
import eu.tax_rate.app.rest.model.ApplicableTaxRate
import eu.tax_rate.app.rest.model.CreateTaxRate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import spock.lang.Shared
import spock.lang.Specification

import java.time.LocalDate

import static org.springframework.http.MediaType.APPLICATION_JSON

@SpringBootTest
@ComponentScan(basePackageClasses = [TaxRateApplication])
@AutoConfigureMockMvc(print = MockMvcPrint.LOG_DEBUG)
@ActiveProfiles(["test", "dev"])
class TaxRateRestControllerSpringBootSpec extends Specification {

    @Autowired
    MockMvc mockMvc

    @Autowired
    MongoTemplate mongoTemplate

    @Shared
    ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    def setup() {
        mongoTemplate.dropCollection(TaxRateDocument)
    }

    def "verify requests to add tax rate ending with bad request"() {
        when: "call get tax rate without mandatory parameter"
            def url = "/api/v1/tax-rates"
            def response =
                mockMvc.
                    perform(
                        MockMvcRequestBuilders
                            .get(url)
                            .param(firstMandatoryParameter, firstParameterValue)
                            .param(secondMandatoryParameter, secondParameterValue)
                    )

        then: "response status indicates bad request error"
            response
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(APPLICATION_JSON))
                .andReturn()

        where: "combinations of parameters missing at least one mandatory parameter"
            firstMandatoryParameter | firstParameterValue | secondMandatoryParameter | secondParameterValue
            "municipalityName"      | "Copenhagen"        | "invalid"                | "invalid"
            "validForDate"          | "2024-01-01"        | "invalid"                | "invalid"
            "municipalityName"      | "Copenhagen"        | "validForDate"           | "ill-formatted"
    }

    def "verify requests to get tax rate on empty database"() {
        when: "call get tax rate with correct parameter values"
            def url = "/api/v1/tax-rates"
            def response =
                mockMvc.
                    perform(
                        MockMvcRequestBuilders
                            .get(url)
                            .param(firstMandatoryParameter, firstParameterValue)
                            .param(secondMandatoryParameter, secondParameterValue)
                    )

        then: "response status indicates not found"
            response
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn()

        where: "combinations of parameters with all mandatory parameters"
            firstMandatoryParameter | firstParameterValue | secondMandatoryParameter | secondParameterValue
            "municipalityName"      | "Copenhagen"        | "validForDate"           | "2024-01-01"
            "municipalityName"      | ""                  | "validForDate"           | "2024-01-01"
    }

    def "verify requests to add tax rate ending with success"() {
        given: "url and other standard things"
            def url = "/api/v1/tax-rates"

        when: "call to add tax rate"
            def createTaxRateResponse =
                mockMvc.perform(
                    MockMvcRequestBuilders
                        .post(url)
                        .contentType(APPLICATION_JSON)
                        .content(
                            objectMapper.writeValueAsString(
                                CreateTaxRate.builder()
                                    .taxRate("0.1")
                                    .municipalityName("Copenhagen")
                                    .validFromDate(LocalDate.parse("2024-01-01"))
                                    .validToDate(LocalDate.parse("2024-01-01"))
                                    .build()
                            )
                        )
                )

        then: "call was successful"
            createTaxRateResponse
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()

        when: "call get tax rate with correct parameter values"
            def response =
                mockMvc.
                    perform(
                        MockMvcRequestBuilders
                            .get(url)
                            .param(firstMandatoryParameter, firstParameterValue)
                            .param(secondMandatoryParameter, secondParameterValue)
                    )

        then: "response status indicates okay status"
            response
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(
                    objectMapper.writeValueAsString(
                        ApplicableTaxRate.builder()
                            .taxRate("0.1")
                            .municipalityName("Copenhagen")
                            .validForDate(LocalDate.parse("2024-01-01"))
                            .build()
                    )
                ))
                .andReturn()

        where: "combinations of parameters with all mandatory parameters"
            firstMandatoryParameter | firstParameterValue | secondMandatoryParameter | secondParameterValue
            "municipalityName"      | "Copenhagen"        | "validForDate"           | "2024-01-01"
    }
}