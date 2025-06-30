package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoJsonTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void testSerializeItemRequestDto() throws Exception {
        Long requestId = 100L;
        String description = "Need a smth";
        LocalDateTime created = LocalDateTime.of(2025, 6, 30, 15, 45);

        ItemRequestDto.ItemAnswerDto answer1 = new ItemRequestDto.ItemAnswerDto();
        String answer1Name = "A";
        answer1.setId(1L);
        answer1.setName(answer1Name);
        answer1.setOwnerId(10L);

        ItemRequestDto.ItemAnswerDto answer2 = new ItemRequestDto.ItemAnswerDto();
        answer2.setId(2L);
        answer2.setName("B");
        answer2.setOwnerId(20L);

        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(requestId);
        dto.setDescription(description);
        dto.setCreated(created);
        dto.setItems(List.of(answer1, answer2));

        var jsonContent = json.write(dto);

        assertThat(jsonContent).hasJsonPath("$.id");
        assertThat(jsonContent).hasJsonPath("$.description");
        assertThat(jsonContent).hasJsonPath("$.created");
        assertThat(jsonContent).hasJsonPath("$.items");

        assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(requestId.intValue());
        assertThat(jsonContent).extractingJsonPathStringValue("$.description").isEqualTo(description);
        assertThat(jsonContent).extractingJsonPathStringValue("$.created")
                .isEqualTo(created.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        assertThat(jsonContent).extractingJsonPathArrayValue("$.items").hasSize(2);
        assertThat(jsonContent).extractingJsonPathNumberValue("$.items[0].id").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathStringValue("$.items[0].name").isEqualTo(answer1Name);
        assertThat(jsonContent).extractingJsonPathNumberValue("$.items[0].ownerId").isEqualTo(10);
    }
}
