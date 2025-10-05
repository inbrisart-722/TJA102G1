package com.util;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class GoogleGeocodeService {
	
    private final String GOOGLE_API_KEY;
	
	public GoogleGeocodeService(@Value("${google.api.key.inbrisart}") String key) { 
		this.GOOGLE_API_KEY = key;
	}
	
    public double[] getLatLngFromGoogle(String address) throws Exception {
        String encodedAddress = URLEncoder.encode(address, "UTF-8");
        String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + encodedAddress + "&key=" + GOOGLE_API_KEY;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response.body());

        if (!root.path("status").asText().equals("OK")) {
            throw new RuntimeException("Geocoding failed: " + root.path("status").asText());
        }

        JsonNode location = root.path("results").get(0).path("geometry").path("location");
        double lat = location.path("lat").asDouble();
        double lng = location.path("lng").asDouble();

        return new double[]{lat, lng};
    }
}
