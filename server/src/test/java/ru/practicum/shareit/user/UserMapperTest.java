package ru.practicum.shareit.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;


@DisplayName("UserMapper test")
class UserMapperTest {

    @Test
    @DisplayName("toDto should return null if input user is null")
    void toDto_shouldReturnNull_ifUserIsNull() {
        assertThat(UserMapper.toDto(null)).isNull();
    }

    @Test
    @DisplayName("fromDto should return null if input dto is null")
    void fromDto_shouldReturnNull_ifDtoIsNull() {
        assertThat(UserMapper.fromDto(null)).isNull();
    }

    @Test
    @DisplayName("toDto should map fields correctly")
    void toDto_shouldMapFields() {
        User user = new User();
        user.setId(1L);
        user.setName("A");
        user.setEmail("a@example.com");

        UserDto dto = UserMapper.toDto(user);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("A");
        assertThat(dto.getEmail()).isEqualTo("a@example.com");
    }
}
