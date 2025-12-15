package com.reservio.reservation_system.reservationsystemdesktop.model.user;

public record AdminPasswordChangeDto(
        Long userId,
        String newPassword
) {
}
