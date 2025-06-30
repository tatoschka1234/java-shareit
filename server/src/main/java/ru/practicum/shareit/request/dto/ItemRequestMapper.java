package ru.practicum.shareit.request.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@UtilityClass
public class ItemRequestMapper {

    public ItemRequestDto toDto(ItemRequest request, List<ItemRequestDto.ItemAnswerDto> answers) {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(request.getId());
        dto.setDescription(request.getDescription());
        dto.setCreated(request.getCreated());
        dto.setItems(answers);
        return dto;
    }

    public List<ItemRequestDto> toDtosWithAnswers(List<ItemRequest> requests, List<Item> allItems) {
        Map<Long, List<ItemRequestDto.ItemAnswerDto>> answersByRequestId = allItems.stream()
                .filter(item -> item.getRequest() != null)
                .collect(Collectors.groupingBy(
                        item -> item.getRequest().getId(),
                        Collectors.mapping(item -> {
                            ItemRequestDto.ItemAnswerDto dto = new ItemRequestDto.ItemAnswerDto();
                            dto.setId(item.getId());
                            dto.setName(item.getName());
                            dto.setOwnerId(item.getOwner().getId());
                            return dto;
                        }, Collectors.toList())
                ));

        return requests.stream()
                .map(req -> toDto(req, answersByRequestId.getOrDefault(req.getId(), List.of())))
                .collect(Collectors.toList());
    }

    public ItemRequestDto toDtoWithAnswers(ItemRequest request, List<Item> items) {
        List<ItemRequestDto.ItemAnswerDto> answers = items.stream()
                .map(item -> {
                    ItemRequestDto.ItemAnswerDto dto = new ItemRequestDto.ItemAnswerDto();
                    dto.setId(item.getId());
                    dto.setName(item.getName());
                    dto.setOwnerId(item.getOwner().getId());
                    return dto;
                })
                .collect(Collectors.toList());

        return toDto(request, answers);
    }

}
