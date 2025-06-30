package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.ItemRepositoryJpa;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserRepositoryJpa;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRequestServiceImplIntegrationTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private UserRepositoryJpa userRepository;

    @Autowired
    private ItemRequestRepository requestRepository;

    @Autowired
    private ItemRepositoryJpa itemRepository;

    private User requester;
    private User responder;

    @BeforeEach
    void setup() {
        requester = new User();
        requester.setName("Requester");
        requester.setEmail("requester@example.com");
        userRepository.save(requester);

        responder = new User();
        responder.setName("Responder");
        responder.setEmail("responder@example.com");
        userRepository.save(responder);
    }

    @Test
    void create_shouldReturnSavedRequest() {
        String description = "Need a thing1!";

        ItemRequestDto result = itemRequestService.create(description, requester.getId());

        assertThat(result.getId()).isNotNull();
        assertThat(result.getDescription()).isEqualTo(description);
        assertThat(result.getItems()).isEmpty();
    }

    @Test
    void getByRequester_shouldReturnRequestsWithAnswers() {
        ItemRequestDto requestDto = itemRequestService.create("Need a tool", requester.getId());
        String itemName = "Tool";
        Item item = new Item();
        item.setName(itemName);
        item.setDescription("cool");
        item.setAvailable(true);
        item.setOwner(responder);
        item.setRequest(requestRepository.findById(requestDto.getId()).get());
        itemRepository.save(item);

        List<ItemRequestDto> result = itemRequestService.getByRequester(requester.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getItems()).hasSize(1);
        assertThat(result.get(0).getItems().get(0).getName()).isEqualTo(itemName);
    }

    @Test
    void getById_shouldReturnRequestWithAnswers() {
        String itemName = "Bike";
        ItemRequestDto requestDto = itemRequestService.create("Need a bike", requester.getId());

        Item item = new Item();
        item.setName(itemName);
        item.setDescription("Old nice bike");
        item.setAvailable(true);
        item.setOwner(responder);
        item.setRequest(requestRepository.findById(requestDto.getId()).get());
        itemRepository.save(item);

        ItemRequestDto result = itemRequestService.getById(requestDto.getId(), requester.getId());

        assertThat(result.getId()).isEqualTo(requestDto.getId());
        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0).getName()).isEqualTo(itemName);
    }
}
