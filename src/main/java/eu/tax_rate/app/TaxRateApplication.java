package eu.tax_rate.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@EnableScheduling
@EnableMongoAuditing
@SpringBootApplication
public class TaxRateApplication {
    public static void main(String[] args) {
        SpringApplication.run(TaxRateApplication.class, args);
    }
}