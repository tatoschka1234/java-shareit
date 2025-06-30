package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ItemDtoJsonTest {

    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    void testSerializeItemDto() throws Exception {
        LocalDateTime now = LocalDateTime.of(2024, 6, 30, 12, 0);

        BookingShortDto lastBooking = new BookingShortDto(1L, 2L);
        BookingShortDto nextBooking = new BookingShortDto(3L, 4L);
        String commentText = UUID.randomUUID().toString();
        String itemName = UUID.randomUUID().toString();

        CommentDto comment = new CommentDto();
        comment.setId(5L);
        comment.setText(commentText);
        comment.setAuthorName("A");
        comment.setCreated(now);
        comment.setItemId(100L);

        ItemDto itemDto = new ItemDto();
        itemDto.setId(10L);
        itemDto.setName(itemName);
        itemDto.setDescription("cool cool cool cool");
        itemDto.setAvailable(true);
        itemDto.setOwnerId(99L);
        itemDto.setRequestId(77L);
        itemDto.setLastBooking(lastBooking);
        itemDto.setNextBooking(nextBooking);
        itemDto.setComments(List.of(comment));

        JsonContent<ItemDto> result = json.write(itemDto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.name").extractingJsonPathStringValue("$.name").isEqualTo(itemName);
        assertThat(result).hasJsonPath("$.available").extractingJsonPathBooleanValue("$.available").isTrue();
        assertThat(result).hasJsonPath("$.lastBooking.id").extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(1);
        assertThat(result).hasJsonPath("$.comments[0].text").extractingJsonPathStringValue("$.comments[0].text").isEqualTo(commentText);
        assertThat(result).hasJsonPath("$.comments[0].created")
                .extractingJsonPathStringValue("$.comments[0].created")
                .isEqualTo(now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }
}

