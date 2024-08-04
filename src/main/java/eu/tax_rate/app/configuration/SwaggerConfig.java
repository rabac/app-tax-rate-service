package eu.tax_rate.app.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("swagger")
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Tax Rate Service REST API")
                        .description("This API provides various tax rates across municipalities and different dates.")
                        .version("1.0")
                        .contact(new Contact().name("TaxRates").url("http://www.taxrates.eu").email("test@taxrates.eu"))
                        .license(new License().name("License of API"))
                );
    }
}
