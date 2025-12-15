package com.reservio.reservation_system.reservationsystemdesktop.model.user;

public record EmployeeDto (
    Long id,
    String firstName,
    String lastName,
    String email,
    String phoneNumber,
    String city,
    String zipCode,
    String street
){}
