package edu.aubg.ics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class IcsApplication {

	public static void main(String[] args) throws IOException {
		SpringApplication.run(IcsApplication.class, args);
	}
}
