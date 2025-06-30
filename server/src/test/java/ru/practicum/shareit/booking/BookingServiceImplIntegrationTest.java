package ru.practicum.shareit.booking;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.item.ItemRepositoryJpa;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepositoryJpa;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingServiceImplIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepositoryJpa userRepository;

    @Autowired
    private ItemRepositoryJpa itemRepository;

    @Autowired
    private BookingRepositoryJpa bookingRepository;

    private User owner;
    private User booker;
    private User otherUser;
    private Item item;
    private Booking booking;

    @BeforeEach
    void setup() {
        owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@example.com");
        owner = userRepository.save(owner);

        booker = new User();
        booker.setName("Booker");
        booker.setEmail("booker@example.com");
        booker = userRepository.save(booker);

        item = new Item();
        item.setName("Bike");
        item.setDescription("Mountain bike very cool!");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);
    }

    @Test
    void createBooking_shouldReturnSavedBooking() {
        BookingRequestDto dto = new BookingRequestDto();
        dto.setItemId(item.getId());
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto result = bookingService.create(dto, booker.getId());

        assertThat(result.getId()).isNotNull();
        assertThat(result.getBooker().getId()).isEqualTo(booker.getId());
        assertThat(result.getItem().getId()).isEqualTo(item.getId());
    }

    @Test
    void approve_shouldChangeStatusToApproved() {
        BookingRequestDto dto = new BookingRequestDto();
        dto.setItemId(item.getId());
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto created = bookingService.create(dto, booker.getId());
        BookingDto approved = bookingService.approve(created.getId(), owner.getId(), true);

        assertThat(approved.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void getById_shouldReturnBooking() {
        BookingRequestDto dto = new BookingRequestDto();
        dto.setItemId(item.getId());
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto created = bookingService.create(dto, booker.getId());
        BookingDto result = bookingService.getById(created.getId());

        assertThat(result.getId()).isEqualTo(created.getId());
    }

    @Test
    void getBookingsByUser_shouldReturnListOfBookings() {
        BookingRequestDto dto = new BookingRequestDto();
        dto.setItemId(item.getId());
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));

        bookingService.create(dto, booker.getId());

        List<BookingDto> bookings = bookingService.getBookingsByUser(booker.getId());

        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getBooker().getId()).isEqualTo(booker.getId());
    }

    @Test
    @DisplayName("approve() should throw AccessDeniedException when called by non-owner")
    void approve_shouldThrowAccessDenied_whenUserIsNotOwner() {
        otherUser = new User();
        otherUser.setName("Other User");
        otherUser.setEmail("other@example.com");
        userRepository.save(otherUser);

        booking = new Booking();
        booking.setItem(item);
        booking.setBooker(otherUser);
        booking.setStatus(BookingStatus.WAITING);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking = bookingRepository.save(booking);

        assertThatThrownBy(() -> bookingService.approve(booking.getId(), otherUser.getId(), true))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("Only the owner can approve or reject booking.");
    }
}
