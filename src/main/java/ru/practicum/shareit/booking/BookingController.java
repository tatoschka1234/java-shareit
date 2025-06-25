package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.util.List;

import static ru.practicum.shareit.util.AppConstants.USER_ID;


@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@RequestBody @Valid BookingRequestDto bookingRequestDto,
                                    @RequestHeader(USER_ID) Long userId) {
        return bookingService.create(bookingRequestDto, userId);
    }

    @GetMapping("/{id}")
    public BookingDto getBooking(@PathVariable Long id) {
        return bookingService.getById(id);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@PathVariable Long bookingId,
                                     @RequestParam boolean approved,
                                     @RequestHeader(USER_ID) Long ownerId) {
        return bookingService.approve(bookingId, ownerId, approved);
    }

    @GetMapping
    public List<BookingDto> getBookingsByUser(@RequestHeader(USER_ID) Long userId) {
        return bookingService.getBookingsByUser(userId);
    }

}
