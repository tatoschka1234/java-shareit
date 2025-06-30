package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingShortDto;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    public interface Create {
    }

    public interface Update {
    }

    private Long id;

    @NotBlank(groups = Create.class, message = "Name must not be blank")
    private String name;

    @NotBlank(groups = Create.class, message = "Description must not be blank")
    private String description;

    @NotNull(groups = Create.class, message = "Availability must be specified")
    private Boolean available;
    private Long ownerId;
    private Long requestId;

    private BookingShortDto lastBooking;
    private BookingShortDto nextBooking;
    private List<CommentDto> comments;

}
