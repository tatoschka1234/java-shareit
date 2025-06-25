package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepositoryJpa extends JpaRepository<Booking, Long> {

    boolean existsByItemIdAndBookerIdAndEndBefore(Long itemId, Long bookerId, LocalDateTime time);

    List<Booking> findByItemIdInAndStartBefore(List<Long> itemIds, LocalDateTime now, Sort sort);

    List<Booking> findByItemIdInAndStartAfter(List<Long> itemIds, LocalDateTime now, Sort sort);

    List<Booking> findByItemIdAndStartBefore(Long itemId, LocalDateTime now, Sort sort);

    List<Booking> findByItemIdAndStartAfter(Long itemId, LocalDateTime now, Sort sort);

    List<Booking> findByBookerId(Long bookerId, Sort sort);
}
