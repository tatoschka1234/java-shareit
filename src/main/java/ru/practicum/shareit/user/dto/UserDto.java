package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserDto {
    public interface Create {
    }

    public interface Update {
    }

    private Long id;
    private String name;

    @NotBlank(groups = Create.class, message = "Email must not be blank")
    @Email(groups = {Create.class, Update.class}, message = "Email should be valid")
    private String email;
}
