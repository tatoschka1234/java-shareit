package ru.practicum.shareit.user;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserValidator userValidator;

    @Override
    public UserDto create(UserDto userDto) {
        userValidator.validateEmailUniqueness(userDto.getEmail());
        User user = UserMapper.fromDto(userDto);
        return UserMapper.toDto(userRepository.save(user));
    }

    @Override
    public UserDto getById(Long id) {
        return UserMapper.toDto(userRepository.findById(id));
    }

    @Override
    public List<UserDto> getAll() {
        return userRepository.findAll().stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto update(Long id, UserDto userDto) {
        User existing = userRepository.findById(id);
        if (userDto.getName() != null) existing.setName(userDto.getName());
        if (userDto.getEmail() != null &&
                !userDto.getEmail().equalsIgnoreCase(existing.getEmail())) {

            userValidator.validateEmailUniqueness(userDto.getEmail());
            existing.setEmail(userDto.getEmail());
        }

        return UserMapper.toDto(userRepository.save(existing));
    }

    @Override
    public void delete(Long id) {
        userRepository.delete(id);
    }
}

