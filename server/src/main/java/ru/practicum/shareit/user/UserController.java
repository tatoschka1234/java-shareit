package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto createUser(@RequestBody @Validated(UserDto.Create.class) UserDto userDto) {
        log.info("Creating user: {}", userDto);
        return userService.create(userDto);
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable Long id) {
        log.info("Retrieving user: {}", id);
        return userService.getById(id);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("Retrieving all users");
        return userService.getAll();
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@PathVariable Long id,
                              @RequestBody @Validated(UserDto.Update.class) UserDto userDto) {
        log.info("Updating user: {}", userDto);
        return userService.update(id, userDto);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        log.info("Deleting user: {}", id);
        userService.delete(id);
    }
}
