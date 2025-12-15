package com.reservio.reservation_system.reservationsystemdesktop.service;

import com.reservio.reservation_system.reservationsystemdesktop.model.user.AdminPasswordChangeDto;
import com.reservio.reservation_system.reservationsystemdesktop.model.user.EmployeeDto;
import com.reservio.reservation_system.reservationsystemdesktop.util.HttpClient;
import jakarta.inject.Inject;

import java.util.Arrays;
import java.util.List;

public class UserService {

    private static final String BASE_PATH = "/users";

    private final HttpClient httpClient;

    @Inject
    public UserService(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public List<EmployeeDto> getAllEmployees() {
        try {
            EmployeeDto[] employees = httpClient.get(BASE_PATH + "/employees", EmployeeDto[].class);
            return Arrays.asList(employees);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public void updateUserByAdmin(EmployeeDto updateDto) {
        try {
            if (updateDto.id() == null) {
                throw new IllegalArgumentException("User ID cannot be null when updating user");
            }

            httpClient.post(BASE_PATH + "/update", updateDto, Void.class);
            System.out.println("User updated successfully: ID=" + updateDto.id());

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Nie udało się zaktualizować użytkownika o ID " + updateDto.id(), e);
        }
    }

    public void changeUserPasswordByAdmin(Long userId, String newPassword) {
        try {
            if (userId == null) {
                throw new IllegalArgumentException("User ID cannot be null");
            }
            if (newPassword == null || newPassword.isBlank()) {
                throw new IllegalArgumentException("New password cannot be empty");
            }

            AdminPasswordChangeDto dto = new AdminPasswordChangeDto(userId, newPassword);

            httpClient.post(BASE_PATH + "/password/change", dto, Void.class);
            System.out.println("Password changed successfully for user ID=" + userId);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Nie udało się zmienić hasła użytkownika o ID " + userId, e);
        }
    }

    public void createUserByAdmin(EmployeeDto newUserDto) {
        try {
            httpClient.post(BASE_PATH + "/create/employee", newUserDto, Void.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}