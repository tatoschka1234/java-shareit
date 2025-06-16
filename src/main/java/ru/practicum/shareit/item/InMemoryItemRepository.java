package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class InMemoryItemRepository implements ItemRepository {

    private final Map<Long, Item> items = new HashMap<>();
    private long nextId = 1;

    @Override
    public Item save(Item item) {
        if (item.getId() == null) {
            item.setId(nextId++);
        }
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Optional<Item> findById(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public List<Item> findByOwnerId(Long ownerId) {
        return items.values().stream()
                .filter(item -> item.getOwner() != null &&
                        ownerId.equals(item.getOwner().getId()))
                .collect(Collectors.toList());
    }


    @Override
    public List<Item> searchByText(String text) {
        String lower = text.toLowerCase();
        return items.values().stream()
                .filter(item -> (item.getName() != null && item.getName().toLowerCase().contains(lower)) ||
                        (item.getDescription() != null && item.getDescription().toLowerCase().contains(lower)))
                .collect(Collectors.toList());
    }
}
