package com.reservio.reservation_system.reservationsystemdesktop.service;

import com.reservio.reservation_system.reservationsystemdesktop.model.user.AdminPasswordChangeDto;
import com.reservio.reservation_system.reservationsystemdesktop.model.user.EmployeeDto;
import com.reservio.reservation_system.reservationsystemdesktop.util.HttpClient;
import jakarta.inject.Inject;

import java.util.List;

public class UserService {

    private final HttpClient httpClient;

    @Inject
    public UserService(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public List<EmployeeDto> getAllEmployees() {
        try {
            return List.of(httpClient.get("/users/employees", EmployeeDto[].class));
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public void changeUserPasswordByAdmin(Long userId, String newPassword) {
        try {
            String url = "/users/" + userId + "/password";

            AdminPasswordChangeDto dto = new AdminPasswordChangeDto(newPassword);
            System.out.println("Sending JSON manually: {\"newPassword\":\"" + newPassword + "\"}");

            httpClient.patch(url, dto, Void.class);

            System.out.println("Sent changePasswordByAdmin request: userId=" + userId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Nie udało się zmienić hasła użytkownika o ID " + userId, e);
        }
    }

}
