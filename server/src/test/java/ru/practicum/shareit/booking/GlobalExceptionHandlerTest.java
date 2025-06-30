package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.AccessDeniedException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static ru.practicum.shareit.util.AppConstants.USER_ID;


@WebMvcTest(BookingController.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Test
    void shouldReturn403_whenAccessDeniedExceptionThrown() throws Exception {
        Mockito.when(bookingService.approve(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean()))
                .thenThrow(new AccessDeniedException("Only the owner can approve or reject booking."));

        mockMvc.perform(patch("/bookings/1?approved=true")
                        .header(USER_ID, 42))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Access denied"))
                .andExpect(jsonPath("$.message").value("Only the owner can approve or reject booking."));
    }
}

