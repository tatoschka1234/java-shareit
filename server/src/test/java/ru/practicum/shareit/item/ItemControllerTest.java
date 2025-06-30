package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.util.AppConstants;

import java.util.List;
import java.util.Locale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String ITEM_NAME = "Item";
    private static final String ITEM_DESCRIPTION = "Super item";

    @Test
    @DisplayName("POST /items should create item")
    void createItem() throws Exception {
        ItemDto item = new ItemDto();
        item.setId(1L);
        item.setName(ITEM_NAME);
        item.setDescription(ITEM_DESCRIPTION);
        item.setAvailable(true);

        Mockito.when(itemService.create(any(), eq(1L))).thenReturn(item);

        mockMvc.perform(post("/items")
                        .header(AppConstants.USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value(ITEM_NAME));
    }

    @Test
    void getItemById() throws Exception {
        ItemDto item = new ItemDto();
        item.setId(1L);
        item.setName(ITEM_NAME);

        Mockito.when(itemService.getById(1L, 1L)).thenReturn(item);

        mockMvc.perform(get("/items/1")
                        .header(AppConstants.USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void updateItem() throws Exception {
        ItemDto item = new ItemDto();
        item.setId(1L);
        item.setName("Updated");

        Mockito.when(itemService.update(eq(1L), any(), eq(1L))).thenReturn(item);

        mockMvc.perform(patch("/items/1")
                        .header(AppConstants.USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"));
    }

    @Test
    void getItemsByOwner() throws Exception {
        ItemDto item = new ItemDto();
        item.setId(1L);
        item.setName(ITEM_NAME);

        Mockito.when(itemService.getByOwner(1L)).thenReturn(List.of(item));

        mockMvc.perform(get("/items")
                        .header(AppConstants.USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void searchItems() throws Exception {
        ItemDto item = new ItemDto();
        item.setId(1L);
        item.setName(ITEM_NAME);

        Mockito.when(itemService.search(ITEM_NAME.toLowerCase(Locale.ROOT))).thenReturn(List.of(item));

        mockMvc.perform(get("/items/search")
                        .param("text", ITEM_NAME.toLowerCase(Locale.ROOT)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(ITEM_NAME));
    }

    @Test
    void addComment() throws Exception {
        CommentDto comment = new CommentDto();
        comment.setId(1L);
        comment.setText("Great item!");
        comment.setAuthorName("Alice");

        Mockito.when(itemService.addComment(eq(1L), eq(1L), any())).thenReturn(comment);

        mockMvc.perform(post("/items/1/comment")
                        .header(AppConstants.USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(comment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Great item!"));
    }
}
