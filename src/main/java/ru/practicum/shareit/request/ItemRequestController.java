package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

import static ru.practicum.shareit.util.AppConstants.USER_ID;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto createRequest(@RequestBody @Valid ItemRequestCreateDto dto,
                                        @RequestHeader(USER_ID) Long userId) {
        return itemRequestService.create(dto.getDescription(), userId);
    }

    @GetMapping
    public List<ItemRequestDto> getOwnRequests(@RequestHeader(USER_ID) Long userId) {
        return itemRequestService.getByRequester(userId);
    }


    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@PathVariable Long requestId,
                                         @RequestHeader(USER_ID) Long userId) {
        return itemRequestService.getById(requestId, userId);
    }
}
