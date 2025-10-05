package com.util;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class OpenStreetMapUtil {

	public static double[] getLatLngFromOSM(String address) throws Exception {
	    String encodedAddress = java.net.URLEncoder.encode(address, java.nio.charset.StandardCharsets.UTF_8);
	    String url = "https://nominatim.openstreetmap.org/search?q=" + encodedAddress + "&format=json";

	    HttpClient client = HttpClient.newHttpClient();
	    HttpRequest request = HttpRequest.newBuilder()
	            .uri(URI.create(url))
	            .header("User-Agent", "Eventra-App")
	            .build();
	    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

	    ObjectMapper mapper = new ObjectMapper();
	    JsonNode root = mapper.readTree(response.body());
	    if (root.isEmpty()) throw new RuntimeException("Address not found");

	    double lat = root.get(0).path("lat").asDouble();
	    double lon = root.get(0).path("lon").asDouble();
	    return new double[]{lat, lon};
	}
	
}
