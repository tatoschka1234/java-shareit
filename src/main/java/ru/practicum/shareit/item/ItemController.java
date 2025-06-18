package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

import static ru.practicum.shareit.util.AppConstants.USER_ID;


@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestBody @Validated(ItemDto.Create.class) ItemDto itemDto,
                              @RequestHeader(USER_ID) Long ownerId) {
        log.info("POST /items by user {}", ownerId);
        log.debug("Creating item: {}", itemDto);
        return itemService.create(itemDto, ownerId);
    }

    @PatchMapping("/{id}")
    public ItemDto updateItem(@PathVariable Long id,
                              @RequestBody @Validated(ItemDto.Update.class) ItemDto itemDto,
                              @RequestHeader(USER_ID) Long ownerId) {
        log.info("PATCH /items/{} by user {}", id, ownerId);
        log.debug("Update payload: {}", itemDto);
        return itemService.update(id, itemDto, ownerId);
    }

    @GetMapping("/{id}")
    public ItemDto getItemById(@PathVariable Long id,
                               @RequestHeader(USER_ID) Long requesterId) {
        log.info("GET /items/{} requested by user {}", id, requesterId);
        return itemService.getById(id, requesterId);
    }

    @GetMapping
    public List<ItemDto> getItemsByOwner(@RequestHeader(USER_ID) Long ownerId) {
        log.info("GET /items by owner {}", ownerId);
        return itemService.getByOwner(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        log.info("GET /items/search?text={}", text);
        return itemService.search(text);
    }
}
