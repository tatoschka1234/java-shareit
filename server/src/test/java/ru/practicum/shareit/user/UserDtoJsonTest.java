package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserDtoJsonTest {

    @Autowired
    private JacksonTester<UserDto> json;

    @Test
    void testSerializeUserDto() throws Exception {
        Long id = 1L;
        String name = "A";
        String email = "a@example.com";

        UserDto dto = new UserDto();
        dto.setId(id);
        dto.setName(name);
        dto.setEmail(email);

        var jsonContent = json.write(dto);

        assertThat(jsonContent).hasJsonPath("$.id");
        assertThat(jsonContent).hasJsonPath("$.name");
        assertThat(jsonContent).hasJsonPath("$.email");

        assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(id.intValue());
        assertThat(jsonContent).extractingJsonPathStringValue("$.name").isEqualTo(name);
        assertThat(jsonContent).extractingJsonPathStringValue("$.email").isEqualTo(email);
    }
}
