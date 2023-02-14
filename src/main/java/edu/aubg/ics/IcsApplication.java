package edu.aubg.ics;

import edu.aubg.ics.api.Connector;
import edu.aubg.ics.api.ImaggaConnector;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class IcsApplication {

	public static void main(String[] args) {
		Connector connector = new ImaggaConnector();
		connector.connect();
		SpringApplication.run(IcsApplication.class, args);
	}

}
