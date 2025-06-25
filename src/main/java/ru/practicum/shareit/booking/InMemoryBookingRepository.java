package ru.practicum.shareit.booking;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class InMemoryBookingRepository implements BookingRepository {

    private final Map<Long, Booking> bookings = new HashMap<>();
    private long nextId = 1;

    @Override
    public Booking save(Booking booking) {
        if (booking.getId() == null) {
            booking.setId(nextId++);
        }
        bookings.put(booking.getId(), booking);
        return booking;
    }

    @Override
    public Optional<Booking> findById(Long id) {
        return Optional.ofNullable(bookings.get(id));
    }

    @Override
    public List<Booking> findAll() {
        return new ArrayList<>(bookings.values());
    }
}