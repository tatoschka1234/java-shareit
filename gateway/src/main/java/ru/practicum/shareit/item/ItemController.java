package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;


import static ru.practicum.shareit.util.AppConstants.USER_ID;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(USER_ID) Long ownerId,
                                             @RequestBody @Valid ItemDto itemDto) {
        log.info("Create item by userId={}, item={}", ownerId, itemDto);
        return itemClient.createItem(ownerId, itemDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateItem(@RequestHeader(USER_ID) Long ownerId,
                                             @PathVariable Long id,
                                             @RequestBody ItemDto itemDto) {
        log.info("Update itemId={} by ownerId={}, item={}", id, ownerId, itemDto);
        return itemClient.updateItem(id, ownerId, itemDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItem(@RequestHeader(USER_ID) Long requesterId,
                                          @PathVariable Long id) {
        log.info("Get itemId={} by userId={}", id, requesterId);
        return itemClient.getItem(id, requesterId);
    }

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader(USER_ID) Long ownerId) {
        return itemClient.getItemsByOwner(ownerId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam String text) {
        return itemClient.search(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(USER_ID) Long authorId,
                                             @PathVariable Long itemId,
                                             @RequestBody @Valid CommentDto commentDto) {
        return itemClient.addComment(itemId, authorId, commentDto);
    }


}
