package com.reservio.reservation_system.reservationsystemdesktop.service;

import com.google.inject.Inject;
import com.reservio.reservation_system.reservationsystemdesktop.model.RoomType.AvailableRoomTypeDto;
import com.reservio.reservation_system.reservationsystemdesktop.model.RoomType.RoomTypeDto;
import com.reservio.reservation_system.reservationsystemdesktop.model.RoomType.UpdateRoomTypePriceDto;
import com.reservio.reservation_system.reservationsystemdesktop.util.HttpClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RoomTypeService {

    private final HttpClient httpClient;

    @Inject
    public RoomTypeService(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public List<RoomTypeDto> getAllRoomTypes() {
        try {
            return List.of(httpClient.get("/room-type", RoomTypeDto[].class));
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public void updateRoomTypePrice(Long roomTypeId, BigDecimal newPrice) {
        try {
            UpdateRoomTypePriceDto dto = new UpdateRoomTypePriceDto(roomTypeId, newPrice);
            httpClient.post("/room-type/price", dto, Void.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<AvailableRoomTypeDto> getAvailableRoomTypes(
            LocalDateTime from,
            LocalDateTime to,
            List<Integer> capacity,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            List<String> amenities
    ) {
        try {
            StringBuilder url = new StringBuilder("/room-type/available?");
            if (from != null) url.append("from=").append(from).append("&");
            if (to != null) url.append("to=").append(to).append("&");
            if (capacity != null && !capacity.isEmpty()) {
                url.append("capacity=").append(
                                capacity.stream().map(String::valueOf).collect(Collectors.joining(",")))
                        .append("&");
            }
            if (minPrice != null) url.append("minPrice=").append(minPrice).append("&");
            if (maxPrice != null) url.append("maxPrice=").append(maxPrice).append("&");
            if (amenities != null && !amenities.isEmpty()) {
                url.append("amenities=").append(String.join(",", amenities)).append("&");
            }

            if (url.charAt(url.length() - 1) == '&') {
                url.deleteCharAt(url.length() - 1);
            }

            AvailableRoomTypeDto[] response = httpClient.get(url.toString(), AvailableRoomTypeDto[].class);
            return Arrays.asList(response);

        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
}
