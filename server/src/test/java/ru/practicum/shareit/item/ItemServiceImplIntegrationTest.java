
package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepositoryJpa;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepositoryJpa;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemServiceImplIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserRepositoryJpa userRepository;

    @Autowired
    private ItemRepositoryJpa itemRepository;

    @Autowired
    private BookingRepositoryJpa bookingRepository;

    @Autowired
    private CommentRepository commentRepository;

    private User owner;

    private static final String ITEM_NAME = "Item";
    private static final String ITEM_DESCRIPTION = "Super item";

    @BeforeEach
    void setup() {
        owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@example.com");
        userRepository.save(owner);
    }

    @Test
    void getByOwner_shouldReturnItemWithBookingsAndComments() {
        Item item = new Item();
        item.setName(ITEM_NAME);
        item.setDescription(ITEM_DESCRIPTION);
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);

        Comment comment = new Comment();
        comment.setText("Great tool!");
        comment.setAuthor(owner);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());
        commentRepository.save(comment);

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(owner);
        booking.setStatus(BookingStatus.APPROVED);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        bookingRepository.save(booking);

        List<ItemDto> items = itemService.getByOwner(owner.getId());

        assertThat(items).hasSize(1);
        ItemDto itemDto = items.get(0);
        assertThat(itemDto.getName()).isEqualTo(ITEM_NAME);
        assertThat(itemDto.getComments()).hasSize(1);
        assertThat(itemDto.getNextBooking()).isNotNull();
    }

    @Test
    void create_shouldPersistItem() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("New Item");
        itemDto.setDescription("Description");
        itemDto.setAvailable(true);

        ItemDto created = itemService.create(itemDto, owner.getId());

        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo(itemDto.getName());
        assertThat(created.getAvailable()).isTrue();
    }

    @Test
    void update_shouldChangeItemFields() {
        Item item = new Item();
        item.setName("Old Name");
        item.setDescription("Old Desc");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);

        String newName = "New Name";
        String newDesc = "New Desc";
        ItemDto updateDto = new ItemDto();
        updateDto.setName(newName);
        updateDto.setDescription(newDesc);

        ItemDto updated = itemService.update(item.getId(), updateDto, owner.getId());

        assertThat(updated.getName()).isEqualTo(newName);
        assertThat(updated.getDescription()).isEqualTo(newDesc);
    }

    @Test
    void search_shouldReturnMatchingItems() {
        String itemName = "Hamburger";
        Item item = new Item();
        item.setName(itemName);
        item.setDescription("eat me!");
        item.setAvailable(true);
        item.setOwner(owner);
        itemRepository.save(item);

        List<ItemDto> results = itemService.search(itemName.toLowerCase(Locale.ROOT));

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).containsIgnoringCase(itemName.toLowerCase(Locale.ROOT));
    }

    @Test
    void addComment_shouldSaveCommentIfBooked() {
        String commentText = "Really cool!";
        Item item = new Item();
        item.setName(ITEM_NAME);
        item.setDescription(ITEM_DESCRIPTION);
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(owner);
        booking.setStatus(BookingStatus.APPROVED);
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        bookingRepository.save(booking);

        CommentDto commentDto = new CommentDto();
        commentDto.setText(commentText);

        CommentDto saved = itemService.addComment(item.getId(), owner.getId(), commentDto);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getText()).isEqualTo(commentText);
        assertThat(saved.getAuthorName()).isEqualTo(owner.getName());
    }
}
