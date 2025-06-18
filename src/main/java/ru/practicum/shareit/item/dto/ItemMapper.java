package ru.practicum.shareit.item.dto;


import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;


public class ItemMapper {

    public static ItemDto toDto(Item item) {
        if (item == null) return null;

        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setOwnerId(item.getOwner() != null ? item.getOwner().getId() : null);
        return dto;
    }

    public static Item fromDto(ItemDto dto, User owner) {
        Item item = new Item();
        item.setId(dto.getId());
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setAvailable(dto.getAvailable());
        item.setOwner(owner);
        return item;
    }
}


