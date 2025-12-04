package com.reservio.reservation_system.reservationsystemdesktop.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpClient {

    private final String baseUrl;
    private final ObjectMapper objectMapper;
    private String token;

    public HttpClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.objectMapper = new ObjectMapper();
    }

    public void setToken(String token) {
        this.token = token;
    }

    public <T> T post(String endpoint, Object requestBody, Class<T> responseClass) throws Exception {
        URL url = new URL(baseUrl + endpoint);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");

        if (token != null) {
            con.setRequestProperty("Authorization", "Bearer " + token);
        }

        con.setDoOutput(true);

        try (OutputStream os = con.getOutputStream()) {
            objectMapper.writeValue(os, requestBody);
        }

        try (InputStream is = con.getInputStream()) {
            return objectMapper.readValue(is, responseClass);
        }
    }

    public <T> T get(String endpoint, Class<T> responseClass) throws Exception {
        URL url = new URL(baseUrl + endpoint);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json");

        if (token != null) {
            con.setRequestProperty("Authorization", "Bearer " + token);
        }

        try (InputStream is = con.getInputStream()) {
            return objectMapper.readValue(is, responseClass);
        }
    }

}
