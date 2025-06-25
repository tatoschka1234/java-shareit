package ru.practicum.shareit.booking;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepositoryJpa;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepositoryJpa;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepositoryJpa bookingRepository;
    private final UserRepositoryJpa userRepository;
    private final ItemRepositoryJpa itemRepository;

    public BookingDto create(BookingRequestDto dto, Long bookerId) {
        validateDates(dto.getStart(), dto.getEnd());

        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Item item = itemRepository.findById(dto.getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found"));

        if (!item.getAvailable()) {
            throw new ValidationException("Item is not available for booking.");
        }
        if (item.getOwner().getId().equals(bookerId)) {
            throw new ValidationException("Owner cannot book their own item.");
        }

        Booking booking = new Booking();
        booking.setStart(dto.getStart());
        booking.setEnd(dto.getEnd());
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        return BookingMapper.toDto(bookingRepository.save(booking));
    }


    @Override
    public BookingDto getById(Long id) {
        return bookingRepository.findById(id)
                .map(BookingMapper::toDto)
                .orElseThrow(() -> new NotFoundException("Booking not found"));
    }

    private void validateDates(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            throw new ValidationException("Booking start and end must be provided.");
        }

        if (!end.isAfter(start)) {
            throw new ValidationException("Booking end must be after start.");
        }

        if (start.isBefore(LocalDateTime.now())) {
            throw new ValidationException("Booking start must be in the future.");
        }
    }

    @Override
    public BookingDto approve(Long bookingId, Long ownerId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new AccessDeniedException("Only the owner can approve or reject booking.");
        }

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Booking has already been processed.");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        return BookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    public List<BookingDto> getBookingsByUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        List<Booking> bookings = bookingRepository.findByBookerId(userId,
                Sort.by(Sort.Direction.DESC, "start"));

        return bookings.stream()
                .map(BookingMapper::toDto)
                .collect(Collectors.toList());
    }

}
