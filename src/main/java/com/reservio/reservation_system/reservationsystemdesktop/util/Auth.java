package com.reservio.reservation_system.reservationsystemdesktop.util;

import java.util.Base64;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Auth {

    private static String token;

    private Auth() {}

    public static void setToken(String jwt) {
        token = jwt;
    }

    public static String getToken() {
        return token;
    }

    public static String getRole() {
        if (token == null) return null;
        try {
            String[] parts = token.split("\\.");
            if (parts.length < 2) return null;

            String payload = new String(Base64.getDecoder().decode(parts[1]));
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> map = mapper.readValue(payload, Map.class);

            return (String) map.get("role");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isAdmin() {
        return "ADMIN".equals(getRole());
    }
}
