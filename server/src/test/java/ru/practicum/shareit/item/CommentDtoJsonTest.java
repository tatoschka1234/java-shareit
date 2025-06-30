package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.item.dto.CommentDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CommentDtoJsonTest {

    @Autowired
    private JacksonTester<CommentDto> json;

    @Test
    void testSerializeCommentDto() throws IOException {
        String itemName = UUID.randomUUID().toString();
        String authorName = UUID.randomUUID().toString();
        Long dtoId = 1L;
        Long itemId = 42L;
        LocalDateTime created = LocalDateTime.of(2025, 6, 30, 12, 30, 0);

        CommentDto dto = new CommentDto();
        dto.setId(dtoId);
        dto.setText(itemName);
        dto.setAuthorName(authorName);
        dto.setCreated(created);
        dto.setItemId(itemId);

        var jsonContent = json.write(dto);

        assertThat(jsonContent).hasJsonPath("$.id");
        assertThat(jsonContent).hasJsonPath("$.text");
        assertThat(jsonContent).hasJsonPath("$.authorName");
        assertThat(jsonContent).hasJsonPath("$.created");
        assertThat(jsonContent).hasJsonPath("$.itemId");

        assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(dtoId.intValue());
        assertThat(jsonContent).extractingJsonPathStringValue("$.text").isEqualTo(itemName);
        assertThat(jsonContent).extractingJsonPathStringValue("$.authorName").isEqualTo(authorName);
        assertThat(jsonContent).extractingJsonPathStringValue("$.created")
                .isEqualTo(created.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(jsonContent).extractingJsonPathNumberValue("$.itemId").isEqualTo(itemId.intValue());
    }
}
