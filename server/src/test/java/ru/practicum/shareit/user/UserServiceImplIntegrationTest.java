package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserServiceImplIntegrationTest {
    private static final String USER_NAME = "A B";
    private static final String USER_EMAIL = "a.b@example.com";

    @Autowired
    private UserService userService;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = createUserDto(USER_NAME, USER_EMAIL);
    }

    @Test
    void create_shouldPersistUser() {
        UserDto saved = userService.create(userDto);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo(USER_NAME);
        assertThat(saved.getEmail()).isEqualTo(USER_EMAIL);
    }

    @Test
    void getById_shouldReturnUser() {
        UserDto saved = userService.create(userDto);
        UserDto found = userService.getById(saved.getId());

        assertThat(found).isEqualTo(saved);
    }

    @Test
    void getAll_shouldReturnAllUsers() {
        userService.create(userDto);
        userService.create(createUserDto("C D", "c@example.com"));

        List<UserDto> users = userService.getAll();
        assertThat(users).hasSize(2);
    }

    @Test
    void update_shouldUpdateNameAndEmail() {
        UserDto saved = userService.create(userDto);

        UserDto updatedData = createUserDto("Updated", "updated@example.com");
        UserDto updated = userService.update(saved.getId(), updatedData);

        assertThat(updated.getName()).isEqualTo("Updated");
        assertThat(updated.getEmail()).isEqualTo("updated@example.com");
    }

    @Test
    void delete_shouldRemoveUser() {
        UserDto saved = userService.create(userDto);
        userService.delete(saved.getId());

        assertThatThrownBy(() -> userService.getById(saved.getId()))
                .isInstanceOf(NotFoundException.class);
    }

    private UserDto createUserDto(String name, String email) {
        UserDto dto = new UserDto();
        dto.setName(name);
        dto.setEmail(email);
        return dto;
    }

}
