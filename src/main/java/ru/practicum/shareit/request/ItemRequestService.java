package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(String description, Long requesterId);
    List<ItemRequestDto> getByRequester(Long requesterId);
    ItemRequestDto getById(Long requestId, Long userId);
}
