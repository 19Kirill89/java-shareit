package practic.shareit.item;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import practic.shareit.booking.model.Booking;
import practic.shareit.booking.model.BookingStatus;
import practic.shareit.booking.repository.BookingRepository;
import practic.shareit.exception.NotFoundException;
import practic.shareit.exception.OperationAccessException;
import practic.shareit.item.comment.dto.CommentDto;
import practic.shareit.item.comment.model.Comment;
import practic.shareit.item.comment.repository.CommentRepository;
import practic.shareit.item.dto.ItemDto;
import practic.shareit.item.model.Item;
import practic.shareit.item.repository.ItemRepository;
import practic.shareit.item.service.ItemServiceImpl;
import practic.shareit.request.dto.ItemRequestDto;
import practic.shareit.request.model.ItemRequest;
import practic.shareit.request.service.ItemRequestServiceImpl;
import practic.shareit.user.dto.UserDto;
import practic.shareit.user.model.User;
import practic.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @InjectMocks
    private ItemServiceImpl itemService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ItemRequestServiceImpl requestService;
    private final User user = new User(1L, "User", "user@email.com");
    private final UserDto userDto = new UserDto(1L, "User", "user@email.com");

    private final ItemRequest itemRequest = ItemRequest.builder()
            .id(1L)
            .description("description")
            .requester(user)
            .items(new ArrayList<>())
            .build();
    private final ItemRequestDto itemRequestDto = ItemRequestDto.builder()
            .id(1L)
            .description("description")
            .requester(userDto)
            .items(new ArrayList<>())
            .build();
    private final Item item = Item.builder()
            .id(1L)
            .name("ItemName")
            .description("description")
            .available(true)
            .itemRequest(itemRequest)
            .ownerId(1L)
            .build();
    private final ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("ItemName")
            .description("description")
            .available(true)
            .requestId(1L)
            .build();
    private final List<Booking> bookingList = List.of(Booking.builder()
                    .id(1L).item(item).booker(user)
                    .start(LocalDateTime.now().minusHours(2L))
                    .end(LocalDateTime.now().minusHours(1L))
                    .status(BookingStatus.WAITING).build(),
            Booking.builder()
                    .id(2L).item(item).booker(user)
                    .start(LocalDateTime.now().plusHours(1L))
                    .end(LocalDateTime.now().plusHours(2L))
                    .status(BookingStatus.WAITING).build());

    private final Comment comment = Comment.builder().id(1L).text("Text").item(item).author(user).build();
    private final CommentDto commentDto = CommentDto.builder().id(1L).text("Text").item(itemDto).authorName("User").build();

    @Test
    void createItemWhenAllIsValidThenReturnedExpectedItem() {
        Mockito.when(userService.findUserById(anyLong()))
                .thenReturn(userDto);

        Mockito.when(requestService.findById(anyLong(), anyLong()))
                .thenReturn(itemRequestDto);

        Mockito.when(itemRepository.save(any()))
                .thenReturn(item);

        Assertions.assertEquals(itemService.create(1L, itemDto), itemDto);
    }

    @Test
    void createItemWhenUserIsNotExistThenReturnedNotFoundException() {
        Mockito.when(userService.findUserById(anyLong()))
                .thenThrow(new NotFoundException(String.format("User with ID = %d not found.", 100L)));

        Exception e = assertThrows(NotFoundException.class,
                () -> itemService.create(100L, itemDto));

        assertEquals(e.getMessage(), String.format("User with ID = %d not found.", 100L));
    }

    @Test
    void findByIdWhenParamsIsValidThenReturnedExpectedItem() {
        Mockito.when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        Mockito.when(commentRepository.findByItemId(anyLong()))
                .thenReturn(List.of(comment));

        Mockito.when(bookingRepository.findBookingsItem(anyLong()))
                .thenReturn(bookingList);

        ItemDto requestedItemDto = itemService.findItemById(1L, 1L);

        assertEquals(requestedItemDto.getName(), item.getName());
        assertEquals(requestedItemDto.getDescription(), item.getDescription());
        assertEquals(requestedItemDto.getAvailable(), item.getAvailable());
        Assertions.assertEquals(requestedItemDto.getLastBooking().getId(), 1L);
        Assertions.assertEquals(requestedItemDto.getLastBooking().getBookerId(), 1L);
        Assertions.assertEquals(requestedItemDto.getNextBooking().getId(), 2L);
        Assertions.assertEquals(requestedItemDto.getNextBooking().getBookerId(), 1L);
    }

    @Test
    void findByIdWhenItemNotFoundThenReturnedNotFoundException() {
        Mockito.when(itemRepository.findById(anyLong()))
                .thenThrow(new NotFoundException(String.format("Item with ID = %d not found.", 100L)));

        Exception e = assertThrows(NotFoundException.class, () -> itemService
                .findItemById(100L, 1L));

        assertEquals(e.getMessage(), String.format("Item with ID = %d not found.", 100L));
    }

    @Test
    void findAllUserItemsWhenAllParamsIsValidThenReturnedListItems() {
        Mockito.when(itemRepository.findAllByOwnerId(anyLong(), any()))
                .thenReturn(List.of(item));

        Mockito.when(bookingRepository.findBookingsItem(anyLong()))
                .thenReturn(bookingList);

        List<ItemDto> userItemsList = itemService.findUserItems(1L, 1, 1);

        Assertions.assertEquals(userItemsList.get(0).getLastBooking().getId(), 1L);
        Assertions.assertEquals(userItemsList.get(0).getLastBooking().getBookerId(), 1L);
        Assertions.assertEquals(userItemsList.get(0).getNextBooking().getId(), 2L);
        Assertions.assertEquals(userItemsList.get(0).getNextBooking().getBookerId(), 1L);
    }

    @Test
    void updateItemWhenAllParamsIsValidThenReturnedUpdatedItem() {
        ItemDto itemDtoUpdate = ItemDto.builder()
                .id(1L)
                .name("ItemUpdate")
                .description("DescriptionUpdate")
                .available(true)
                .requestId(1L)
                .build();

        Mockito.when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        Mockito.when(userService.findUserById(anyLong()))
                .thenReturn(userDto);
        Mockito.when(itemRepository.save(any()))
                .thenAnswer(i -> i.getArgument(0));

        Assertions.assertEquals(itemService.save(itemDtoUpdate, 1L, 1L), itemDtoUpdate);
    }

    @Test
    void updateItemWhenUserIsNotOwnerIdThenReturnedOperationAccessException() {
        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        Exception e = assertThrows(OperationAccessException.class,
                () -> itemService.save(itemDto, 1L, 2L));

        assertEquals(e.getMessage(), String.format("User with ID = %d is not an owner, update is not available.", 2L));
    }

    @Test
    void updateItemWhenItemIsNotFoundThenReturnedNotFoundException() {
        Mockito.when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        Exception e = assertThrows(NotFoundException.class,
                () -> itemService.save(itemDto, 100L, 1L));

        assertEquals(e.getMessage(), String.format("Item with ID = %d not found.", 100L));
    }

    @Test
    void searchTestWllParamsIsValidThenReturnedPageableListOfItems() {
        MatcherAssert.assertThat(itemService.search("", 0, 10), hasSize(0));
        MatcherAssert.assertThat(itemService.search(null, 0, 10), hasSize(0));

        Mockito.when(itemRepository.searchAvailableItems(anyString(), any()))
                .thenReturn(List.of(item));

        assertEquals(itemService.search("item", 0, 10), List.of(itemDto));
    }



    @Test
    void addCommentTest() {
        Mockito.when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        Mockito.when(userService.findUserById(anyLong()))
                .thenReturn(userDto);
        Mockito.when(bookingRepository.findByItemIdAndBookerIdAndStatusIsAndEndIsBefore(anyLong(), anyLong(), any(), any()))
                .thenReturn(bookingList);
        Mockito.when(commentRepository.save(any()))
                .thenReturn(comment);
        Mockito.when(commentRepository.save(any()))
                .thenAnswer(i -> i.getArgument(0));
        CommentDto testComment = itemService.addComment(1L, 1L, commentDto);

        assertEquals(testComment.getId(), commentDto.getId());
        Assertions.assertEquals(testComment.getItem(), commentDto.getItem());
        assertEquals(testComment.getText(), commentDto.getText());
        assertEquals(testComment.getAuthorName(), commentDto.getAuthorName());
    }

    @Test
    void deleteItem() {
        itemService.delete(1L);

        Mockito.verify(itemRepository).deleteById(1L);
    }
}