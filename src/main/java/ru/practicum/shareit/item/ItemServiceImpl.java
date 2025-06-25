package ru.practicum.shareit.item;


import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepositoryJpa;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepositoryJpa;
import ru.practicum.shareit.user.model.User;


import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepositoryJpa itemRepository;
    private final UserRepositoryJpa userRepository;
    private final BookingRepositoryJpa bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto create(ItemDto itemDto, Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("User with id " + ownerId + " not found."));

        Item item = ItemMapper.fromDto(itemDto, owner);
        return ItemMapper.toDto(itemRepository.save(item));
    }

    @Override
    public ItemDto update(Long itemId, ItemDto itemDto, Long ownerId) {
        Item existing = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with id " + itemId + " not found."));

        if (!existing.getOwner().getId().equals(ownerId)) {
            throw new AccessDeniedException("Only the owner can update the item.");
        }

        if (itemDto.getName() != null) existing.setName(itemDto.getName());
        if (itemDto.getDescription() != null) existing.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null) existing.setAvailable(itemDto.getAvailable());

        return ItemMapper.toDto(itemRepository.save(existing));
    }

    @Override
    public ItemDto getById(Long id, Long requesterId) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item with id " + id + " not found."));

        ItemDto dto = ItemMapper.toDto(item);


        List<CommentDto> comments = commentRepository.findByItemIdOrderByCreatedDesc(id).stream()
                .map(CommentMapper::toDto)
                .collect(Collectors.toList());
        dto.setComments(comments);


        if (item.getOwner().getId().equals(requesterId)) {
            LocalDateTime now = LocalDateTime.now();

            bookingRepository.findFirstByItemIdAndStartBeforeOrderByStartDesc(id, now)
                    .ifPresent(last -> dto.setLastBooking(
                            new BookingShortDto(last.getId(), last.getBooker().getId())
                    ));

            bookingRepository.findFirstByItemIdAndStartAfterOrderByStartAsc(id, now)
                    .ifPresent(next -> dto.setNextBooking(
                            new BookingShortDto(next.getId(), next.getBooker().getId())
                    ));
        }

        return dto;
    }


    @Override
    public List<ItemDto> getByOwner(Long ownerId) {
        List<Item> items = itemRepository.findByOwnerId(ownerId);
        LocalDateTime now = LocalDateTime.now();

        return items.stream().map(item -> {
            ItemDto dto = ItemMapper.toDto(item);

            List<CommentDto> comments = commentRepository.findByItemIdOrderByCreatedDesc(item.getId())
                    .stream()
                    .map(CommentMapper::toDto)
                    .collect(Collectors.toList());
            dto.setComments(comments);

            bookingRepository.findFirstByItemIdAndStartBeforeOrderByStartDesc(item.getId(), now)
                    .ifPresent(last -> dto.setLastBooking(new BookingShortDto(last.getId(), last.getBooker().getId())));

            bookingRepository.findFirstByItemIdAndStartAfterOrderByStartAsc(item.getId(), now)
                    .ifPresent(next -> dto.setNextBooking(new BookingShortDto(next.getId(), next.getBooker().getId())));

            return dto;
        }).collect(Collectors.toList());
    }


    @Override
    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return itemRepository
                .searchAvailableItems(text)
                .stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto addComment(Long itemId, Long authorId, CommentDto dto) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new NotFoundException("User not found"));


        boolean hasBooked = bookingRepository.existsByItemIdAndBookerIdAndEndBefore(
                itemId, authorId, LocalDateTime.now());

        if (!hasBooked) {
            throw new ValidationException("Only users who booked the item can leave a comment.");
        }

        Comment comment = CommentMapper.fromDto(dto, item, author);
        return CommentMapper.toDto(commentRepository.save(comment));
    }

}
