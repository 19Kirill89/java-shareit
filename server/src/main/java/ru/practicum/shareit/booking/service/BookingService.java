package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.InputBookingDto;
import ru.practicum.shareit.booking.dto.OutputBookingDto;

import java.util.List;

public interface BookingService {
    OutputBookingDto create(InputBookingDto bookingDtoShort, Long bookerId);

    OutputBookingDto findBookingById(Long bookingId, Long userId);

    List<OutputBookingDto> findBookingsByUser(String state, Long userId, Integer from, Integer size);

    List<OutputBookingDto> findBookingsByOwner(String state, Long ownerId, Integer from, Integer size);

    OutputBookingDto approve(long bookingId, long userId, Boolean approve);
}