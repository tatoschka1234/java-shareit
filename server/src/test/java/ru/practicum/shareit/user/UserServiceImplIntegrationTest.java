package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

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

    @Autowired
    private UserRepositoryJpa userRepository;

    private UserDto userDto;
    private User user1;
    private User user2;

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

    @Test
    @DisplayName("update() should throw AlreadyExistsException when email is already taken")
    void updateUser_shouldThrowAlreadyExists_whenEmailTaken() {

        user1 = new User();
        user1.setName("Alice");
        user1.setEmail("alice@example.com");
        userRepository.save(user1);

        user2 = new User();
        user2.setName("Bob");
        user2.setEmail("bob@example.com");
        user2 = userRepository.save(user2);

        UserDto updateDto = new UserDto();
        updateDto.setEmail("alice@example.com");

        assertThatThrownBy(() -> userService.update(user2.getId(), updateDto))
                .isInstanceOf(AlreadyExistsException.class)
                .hasMessageContaining("Email already exists");
    }


    private UserDto createUserDto(String name, String email) {
        UserDto dto = new UserDto();
        dto.setName(name);
        dto.setEmail(email);
        return dto;
    }

}
