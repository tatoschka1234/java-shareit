package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /users should create a user")
    void createUser() throws Exception {
        String userName = "A";
        String userEmail = "a@example.com";
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName(userName);
        userDto.setEmail(userEmail);

        Mockito.when(userService.create(any())).thenReturn(userDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value(userName))
                .andExpect(jsonPath("$.email").value(userEmail));
    }

    @Test
    @DisplayName("GET /users/{id} should return user by id")
    void getUserById() throws Exception {
        String userName = "B";
        String userEmail = "b@example.com";
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName(userName);
        userDto.setEmail(userEmail);

        Mockito.when(userService.getById(1L)).thenReturn(userDto);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value(userName))
                .andExpect(jsonPath("$.email").value(userEmail));
    }

    @Test
    @DisplayName("PATCH /users/{id} should update user")
    void updateUser() throws Exception {
        String userName = "C";
        String userEmail = "c@example.com";
        UserDto updatedDto = new UserDto();
        updatedDto.setId(1L);
        updatedDto.setName(userName);
        updatedDto.setEmail(userEmail);

        Mockito.when(userService.update(eq(1L), any())).thenReturn(updatedDto);

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(userName))
                .andExpect(jsonPath("$.email").value(userEmail));
    }

    @Test
    @DisplayName("GET /users should return all users")
    void getAllUsers() throws Exception {
        String userName = "D";
        String userEmail = "d@example.com";
        UserDto user = new UserDto();
        user.setId(1L);
        user.setName(userName);
        user.setEmail(userEmail);

        Mockito.when(userService.getAll()).thenReturn(List.of(user));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value(userName));
    }

    @Test
    @DisplayName("DELETE /users/{id} should delete user")
    void deleteUser() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        Mockito.verify(userService).delete(1L);
    }
}

