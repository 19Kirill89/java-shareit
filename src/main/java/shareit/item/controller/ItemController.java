package shareit.item.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import shareit.item.comment.dto.CommentDto;
import shareit.item.dto.ItemDto;
import shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
@Slf4j
public class ItemController {
    private static final String OWNER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@RequestHeader(OWNER_ID_HEADER) Long userId, @Valid @RequestBody ItemDto itemDto) {
        log.info("POST-запрос: '/items' добавит предмет к пользователю ID = {}", userId);
        return itemService.create(userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto findById(@RequestHeader(OWNER_ID_HEADER) Long userId, @PathVariable Long itemId) {
        log.info("GET-запрос: '/items' получить вещь с ID = {}", itemId);
        return itemService.findItemById(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> findAll(@RequestHeader(OWNER_ID_HEADER) Long userId) {
        log.info("GET-запрос: '/items'получить все вещи с ID = {}", userId);
        return itemService.findAllUsersItems(userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto save(@RequestHeader(OWNER_ID_HEADER) Long userId, @PathVariable long itemId,
                        @RequestBody ItemDto itemDto) {
        log.info("PATCH-запрос: '/items' обновим вещь с ID = {}", itemId);
        return itemService.save(itemDto, itemId, userId);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@PathVariable Long itemId) {
        log.info("DELETE-запрос: '/items' удалить предмет с ID = {}", itemId);
        itemService.deleteById(itemId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> search(@RequestParam String text) {
        log.info("GET-запрос: '/items/search' ищем элемент с текстом = {}", text);
        return itemService.search(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader(OWNER_ID_HEADER) Long userId,
                                    @PathVariable Long itemId,
                                    @Valid @RequestBody CommentDto commentDto) {
        log.info("POST-запрос: '/items/{itemId}/comment' добавим комент");
        return itemService.addComment(itemId, userId, commentDto);
    }
}