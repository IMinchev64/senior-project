package edu.aubg.ics.config;

import edu.aubg.ics.api.Connector;
import edu.aubg.ics.api.ImaggaConnector;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public Connector imaggaConector() {
        return new ImaggaConnector();
    }
}
