package com.reservio.reservation_system.reservationsystemdesktop.service;

import com.reservio.reservation_system.reservationsystemdesktop.model.room.RoomDto;
import com.reservio.reservation_system.reservationsystemdesktop.model.room.UpdateRoomStatusDto;
import com.reservio.reservation_system.reservationsystemdesktop.util.HttpClient;
import jakarta.inject.Inject;

import java.util.List;

public class RoomService {

    private final HttpClient httpClient;

    @Inject
    public RoomService(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public List<RoomDto> getAllRooms() {
        try {
            return List.of(httpClient.get("/rooms", RoomDto[].class));
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public void updateRoomStatus(Long roomId, String newStatus) {
        try {
            UpdateRoomStatusDto dto = new UpdateRoomStatusDto(newStatus);
            httpClient.patch("/rooms/" + roomId + "/status", dto, Void.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}