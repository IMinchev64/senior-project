package edu.aubg.ics.config;

import edu.aubg.ics.api.Connector;
import edu.aubg.ics.api.ImaggaConnector;
import edu.aubg.ics.dao.ImageDAO;
import edu.aubg.ics.dao.PostgresDAO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.SQLException;

@Configuration
public class AppConfig {
    @Bean
    public Connector imaggaConector() {
        return new ImaggaConnector();
    }

    @Bean
    public PostgresDAO postgresDAO() throws SQLException {
        return new PostgresDAO();
    }

    @Bean
    public ImageDAO imageDAO() {
        return new ImageDAO();
    }
}
