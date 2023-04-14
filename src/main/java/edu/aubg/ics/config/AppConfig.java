package edu.aubg.ics.config;

import edu.aubg.ics.api.Connector;
import edu.aubg.ics.api.ImaggaConnector;
import edu.aubg.ics.dao.ImageDAO;
import edu.aubg.ics.dao.PostgresDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.spring6.ISpringTemplateEngine;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.sql.SQLException;

import static edu.aubg.ics.util.Constants.POSTGRES_ICS_CONNECTION;

@Configuration
public class AppConfig implements WebMvcConfigurer {

    @Autowired
    private ApplicationContext applicationContext;


    @Bean
    public Connector imaggaConector() {
        return new ImaggaConnector();
    }

    @Bean
    public PostgresDAO postgresDAO() throws SQLException {
        return new PostgresDAO(POSTGRES_ICS_CONNECTION);
    }

    @Bean
    public ImageDAO imageDAO() {
        return new ImageDAO();
    }

    @Bean
    public ViewResolver viewResolver() {
        ThymeleafViewResolver resolver = new ThymeleafViewResolver();
        resolver.setTemplateEngine(templateEngine());
        resolver.setCharacterEncoding("UTF-8");
        return resolver;
    }

    @Bean
    public ISpringTemplateEngine templateEngine() {
        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(templateResolver());
        return engine;
    }

    @Bean
    public ITemplateResolver templateResolver() {
        SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setPrefix("classpath:/templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCharacterEncoding("UTF-8");
        return resolver;
    }


}
