package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.shareit.util.AppConstants.USER_ID;


@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /requests should create request")
    void createRequest() throws Exception {
        String requestDescription = "Need a thing";
        ItemRequestDto input = new ItemRequestDto();
        input.setDescription(requestDescription);
        ItemRequestDto response = createItemRequestDto(requestDescription);

        Mockito.when(itemRequestService.create(eq(requestDescription), eq(1L))).thenReturn(response);

        mockMvc.perform(post("/requests")
                        .header(USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value(requestDescription));
    }

    @Test
    @DisplayName("GET /requests should return own requests")
    void getOwnRequests() throws Exception {
        String requestDescription = "Need a tool";
        ItemRequestDto dto = createItemRequestDto(requestDescription);
        Mockito.when(itemRequestService.getByRequester(1L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/requests")
                        .header(USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value(requestDescription));
    }

    @Test
    @DisplayName("GET /requests/{requestId} should return specific request")
    void getRequestById() throws Exception {
        String requestDescription = "Need a thing";
        ItemRequestDto dto = createItemRequestDto(requestDescription);
        Mockito.when(itemRequestService.getById(1L, 1L)).thenReturn(dto);

        mockMvc.perform(get("/requests/1")
                        .header(USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value(requestDescription));
    }

    private ItemRequestDto createItemRequestDto(String description) {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setDescription(description);
        dto.setCreated(LocalDateTime.now());
        dto.setItems(List.of());
        dto.setId(1L);
        return dto;
    }
}
