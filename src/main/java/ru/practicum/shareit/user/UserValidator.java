package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserValidator {

    private final UserRepository userRepository;

    public void validateEmailUniqueness(String email) {
        boolean exists = userRepository.findAll().stream()
                .anyMatch(user -> user.getEmail().equalsIgnoreCase(email));

        if (exists) {
            throw new RuntimeException("Email already exists: " + email);
        }
    }

}
