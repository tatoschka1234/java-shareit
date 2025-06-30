package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepositoryJpa;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.user.UserRepositoryJpa;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository requestRepository;
    private final UserRepositoryJpa userRepository;
    private final ItemRepositoryJpa itemRepository;

    @Override
    @Transactional
    public ItemRequestDto create(String description, Long requesterId) {
        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        ItemRequest request = new ItemRequest();
        request.setDescription(description);
        request.setRequester(requester);
        request.setCreated(LocalDateTime.now());

        return ItemRequestMapper.toDto(requestRepository.save(request), List.of());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getByRequester(Long requesterId) {
        userRepository.findById(requesterId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        List<ItemRequest> requests = requestRepository
                .findByRequesterIdOrderByCreatedDesc(requesterId);

        List<Item> allItems = itemRepository.findByRequestIdIn(
                requests.stream().map(ItemRequest::getId).collect(Collectors.toList())
        );
        return ItemRequestMapper.toDtosWithAnswers(requests, allItems);

    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequestDto getById(Long requestId, Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        ItemRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request not found"));

        List<Item> items = itemRepository.findByRequestId(request.getId());
        return ItemRequestMapper.toDtoWithAnswers(request, items);

    }

}
