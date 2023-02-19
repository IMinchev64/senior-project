package edu.aubg.ics.api;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static edu.aubg.ics.util.Constants.*;

@Component
public class ImaggaConnector implements Connector {
    @Override
    public String connect(String imageUrl) throws IOException {
        String credentialsToEncode = String.format("%s:%s", IMAGGA_API_KEY, IMAGGA_API_SECRET);
        String basicAuth = Base64.getEncoder().encodeToString(credentialsToEncode.getBytes(StandardCharsets.UTF_8));

        String url = String.format("%s?image_url=%s", IMAGGA_ENDPOINT_URL, imageUrl);
        URL urlObject = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();

        connection.setRequestProperty("Authorization", "Basic " + basicAuth);

        int responseCode = connection.getResponseCode();

        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader connectionInput = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        String jsonResponse = connectionInput.readLine();

        connectionInput.close();

        return jsonResponse;
    }
}
