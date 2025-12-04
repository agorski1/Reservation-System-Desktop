package com.reservio.reservation_system.reservationsystemdesktop.service;

import com.reservio.reservation_system.reservationsystemdesktop.model.user.LoginRequestDto;
import com.reservio.reservation_system.reservationsystemdesktop.model.user.LoginResponseDto;
import com.reservio.reservation_system.reservationsystemdesktop.util.HttpClient;
import com.reservio.reservation_system.reservationsystemdesktop.util.Auth;
import jakarta.inject.Inject;

public class AuthService {

    private final HttpClient httpClient;

    @Inject
    public AuthService(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public boolean login(String email, String password) {
        try {
            LoginRequestDto dto = new LoginRequestDto(email, password);
            LoginResponseDto response = httpClient.post("/auth/login", dto, LoginResponseDto.class);

            if (response != null && response.token() != null) {
                String jwt = response.token();
                Auth.setToken(jwt);
                httpClient.setToken(jwt);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
