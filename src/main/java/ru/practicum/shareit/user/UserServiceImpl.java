package ru.practicum.shareit.user;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepositoryJpa userRepository;

    @Override
    @Transactional
    public UserDto create(UserDto userDto) {
        validateEmailUniqueness(userDto.getEmail());
        User user = UserMapper.fromDto(userDto);
        return UserMapper.toDto(userRepository.save(user));
    }

    @Override
    public UserDto getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " not found."));
        return UserMapper.toDto(user);
    }

    @Override
    public List<UserDto> getAll() {
        return userRepository.findAll().stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDto update(Long id, UserDto userDto) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " not found."));

        if (userDto.getName() != null) {
            existing.setName(userDto.getName());
        }

        if (userDto.getEmail() != null &&
                !userDto.getEmail().equalsIgnoreCase(existing.getEmail())) {

            validateEmailUniqueness(userDto.getEmail());
            existing.setEmail(userDto.getEmail());
        }

        return UserMapper.toDto(userRepository.save(existing));
    }

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    private void validateEmailUniqueness(String email) {
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new AlreadyExistsException("Email already exists: " + email);
        }
    }
}
