package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;


import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.shareit.util.AppConstants.USER_ID;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /bookings should create a booking")
    void createBooking() throws Exception {
        BookingRequestDto requestDto = new BookingRequestDto();
        requestDto.setItemId(1L);
        requestDto.setStart(LocalDateTime.now().plusDays(1));
        requestDto.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto responseDto = new BookingDto();
        responseDto.setId(1L);
        responseDto.setStatus(BookingStatus.WAITING);

        Mockito.when(bookingService.create(any(), eq(1L))).thenReturn(responseDto);

        mockMvc.perform(post("/bookings")
                        .header(USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getBookingById() throws Exception {
        BookingDto dto = new BookingDto();
        dto.setId(1L);

        Mockito.when(bookingService.getById(1L)).thenReturn(dto);

        mockMvc.perform(get("/bookings/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void approveBooking() throws Exception {
        BookingDto dto = new BookingDto();
        dto.setId(1L);
        dto.setStatus(BookingStatus.APPROVED);

        Mockito.when(bookingService.approve(1L, 1L, true)).thenReturn(dto);

        mockMvc.perform(patch("/bookings/1")
                        .header(USER_ID, 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void getBookingsByUser() throws Exception {
        BookingDto dto = new BookingDto();
        dto.setId(1L);

        Mockito.when(bookingService.getBookingsByUser(1L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/bookings")
                        .header(USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }
}
