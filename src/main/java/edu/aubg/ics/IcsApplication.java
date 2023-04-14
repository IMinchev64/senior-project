package edu.aubg.ics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.sql.SQLException;

@SpringBootApplication
public class IcsApplication {

	public static void main(String[] args) throws IOException, SQLException {
		//CocoAnnotationParser.connectToDB();
		SpringApplication.run(IcsApplication.class, args);
	}
}
