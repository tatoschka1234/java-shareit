package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.user.dto.UserShortDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoJsonTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void testSerializeBookingDto() throws Exception {
        Long id = 1L;
        Long itemId = 2L;
        String itemName = "Item";
        String itemDescription = "Cool item";

        Long bookerId = 3L;
        String bookerName = "A";
        String bookerEmail = "a@example.com";

        LocalDateTime start = LocalDateTime.of(2025, 7, 1, 10, 0);
        LocalDateTime end = LocalDateTime.of(2025, 7, 2, 10, 0);

        BookingDto dto = new BookingDto();
        dto.setId(id);
        dto.setItem(new ItemShortDto(itemId, itemName, itemDescription));
        dto.setBooker(new UserShortDto(bookerId, bookerName, bookerEmail));
        dto.setStart(start);
        dto.setEnd(end);
        dto.setStatus(BookingStatus.APPROVED);

        var jsonContent = json.write(dto);

        assertThat(jsonContent).hasJsonPath("$.id");
        assertThat(jsonContent).hasJsonPath("$.item.id");
        assertThat(jsonContent).hasJsonPath("$.booker.id");
        assertThat(jsonContent).hasJsonPath("$.start");
        assertThat(jsonContent).hasJsonPath("$.end");
        assertThat(jsonContent).hasJsonPath("$.status");

        assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(id.intValue());
        assertThat(jsonContent).extractingJsonPathNumberValue("$.item.id").isEqualTo(itemId.intValue());
        assertThat(jsonContent).extractingJsonPathStringValue("$.item.name").isEqualTo(itemName);
        assertThat(jsonContent).extractingJsonPathNumberValue("$.booker.id").isEqualTo(bookerId.intValue());
        assertThat(jsonContent).extractingJsonPathStringValue("$.booker.name").isEqualTo(bookerName);
        assertThat(jsonContent).extractingJsonPathStringValue("$.start")
                .isEqualTo(start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(jsonContent).extractingJsonPathStringValue("$.end")
                .isEqualTo(end.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(jsonContent).extractingJsonPathStringValue("$.status").isEqualTo("APPROVED");
    }
}
