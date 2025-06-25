package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.util.List;

public interface BookingService {
    BookingDto create(BookingRequestDto bookingDto, Long bookerId);

    BookingDto getById(Long id);

    List<BookingDto> getAll();

    BookingDto approve(Long bookingId, Long ownerId, boolean approved);

    List<BookingDto> getBookingsByUser(Long userId);
}