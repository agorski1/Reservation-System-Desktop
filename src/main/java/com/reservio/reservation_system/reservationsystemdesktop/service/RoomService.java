package com.reservio.reservation_system.reservationsystemdesktop.service;

import com.reservio.reservation_system.reservationsystemdesktop.model.room.AvailableRoomDto;
import com.reservio.reservation_system.reservationsystemdesktop.model.room.RoomDto;
import com.reservio.reservation_system.reservationsystemdesktop.model.room.UpdateRoomStatusDto;
import com.reservio.reservation_system.reservationsystemdesktop.util.HttpClient;
import jakarta.inject.Inject;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
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

    public List<AvailableRoomDto> getAvailableRooms(Long roomTypeId, LocalDateTime from, LocalDateTime to) {
        try {
            String url = String.format(
                    "/rooms/available?roomTypeId=%d&from=%s&to=%s",
                    roomTypeId,
                    from.toString(),
                    to.toString()
            );

            AvailableRoomDto[] response = httpClient.get(url, AvailableRoomDto[].class);
            return Arrays.asList(response);

        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

}