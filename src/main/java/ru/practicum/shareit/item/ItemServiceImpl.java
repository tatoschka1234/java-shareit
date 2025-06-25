package ru.practicum.shareit.item;


import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepositoryJpa;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
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
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepositoryJpa itemRepository;
    private final UserRepositoryJpa userRepository;
    private final BookingRepositoryJpa bookingRepository;
    private final CommentRepository commentRepository;

    private static final Sort SORT_BY_CREATED_DESC = Sort.by(Sort.Direction.DESC, "created");
    private static final Sort SORT_BY_START_DESC = Sort.by(Sort.Direction.DESC, "start");
    private static final Sort SORT_BY_START_ASC = Sort.by(Sort.Direction.ASC, "start");


    @Override
    @Transactional
    public ItemDto create(ItemDto itemDto, Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("User with id " + ownerId + " not found."));

        Item item = ItemMapper.fromDto(itemDto, owner);
        return ItemMapper.toDto(itemRepository.save(item));
    }

    @Override
    @Transactional
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

        List<CommentDto> comments = commentRepository.findByItemId(id).stream()
                .map(CommentMapper::toDto)
                .collect(Collectors.toList());
        dto.setComments(comments);

        if (item.getOwner().getId().equals(requesterId)) {
            LocalDateTime now = LocalDateTime.now();

            bookingRepository.findByItemIdAndStartBefore(id, now,
                            SORT_BY_START_DESC)
                    .stream()
                    .findFirst()
                    .ifPresent(last -> dto.setLastBooking(
                            new BookingShortDto(last.getId(), last.getBooker().getId()))
                    );

            bookingRepository.findByItemIdAndStartAfter(id, now,
                            SORT_BY_START_ASC)
                    .stream()
                    .findFirst()
                    .ifPresent(next -> dto.setNextBooking(
                            new BookingShortDto(next.getId(), next.getBooker().getId()))
                    );
        }

        return dto;
    }


    @Override
    public List<ItemDto> getByOwner(Long ownerId) {
        List<Item> items = itemRepository.findByOwnerId(ownerId);
        if (items.isEmpty()) return List.of();

        LocalDateTime now = LocalDateTime.now();

        List<Long> itemIds = items.stream()
                .map(Item::getId)
                .collect(Collectors.toList());

        Map<Long, List<CommentDto>> commentsByItem = commentRepository
                .findByItemIdIn(itemIds, SORT_BY_CREATED_DESC)
                .stream()
                .map(CommentMapper::toDto)
                .collect(Collectors.groupingBy(CommentDto::getItemId));


        Map<Long, Booking> lastBookings = bookingRepository
                .findByItemIdInAndStartBefore(itemIds, now, SORT_BY_START_DESC)
                .stream()
                .collect(Collectors.toMap(
                        b -> b.getItem().getId(),
                        Function.identity(),
                        (existing, replacement) -> existing
                ));

        Map<Long, Booking> nextBookings = bookingRepository
                .findByItemIdInAndStartAfter(itemIds, now, SORT_BY_START_ASC)
                .stream()
                .collect(Collectors.toMap(
                        b -> b.getItem().getId(),
                        Function.identity(),
                        (existing, replacement) -> existing
                ));

        return items.stream()
                .map(item -> mapToItemDto(item, commentsByItem, lastBookings, nextBookings))
                .collect(Collectors.toList());
    }

    private ItemDto mapToItemDto(Item item,
                                 Map<Long, List<CommentDto>> commentsByItem,
                                 Map<Long, Booking> lastBookings,
                                 Map<Long, Booking> nextBookings) {
        ItemDto dto = ItemMapper.toDto(item);

        dto.setComments(commentsByItem.getOrDefault(item.getId(), List.of()));

        Booking last = lastBookings.get(item.getId());
        if (last != null) {
            dto.setLastBooking(new BookingShortDto(last.getId(), last.getBooker().getId()));
        }

        Booking next = nextBookings.get(item.getId());
        if (next != null) {
            dto.setNextBooking(new BookingShortDto(next.getId(), next.getBooker().getId()));
        }

        return dto;
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
    @Transactional
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
