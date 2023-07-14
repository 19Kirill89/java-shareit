package practic.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import practic.shareit.exception.NotFoundException;
import practic.shareit.item.repository.ItemRepository;
import practic.shareit.request.dto.ItemRequestDto;
import practic.shareit.request.model.ItemRequest;
import practic.shareit.request.repository.ItemRequestRepository;
import practic.shareit.request.service.ItemRequestServiceImpl;
import practic.shareit.user.dto.UserDto;
import practic.shareit.user.model.User;
import practic.shareit.user.service.UserServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceTest {

    @InjectMocks
    private ItemRequestServiceImpl requestService;

    @Mock
    private ItemRequestRepository requestRepository;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private ItemRepository itemRepository;
    private final User user = new User(1L, "User", "user@email.com");
    private final UserDto userDto = new UserDto(1L, "User", "user@email.com");
    private final ItemRequest itemRequest = ItemRequest.builder()
            .id(1L)
            .requester(user)
            .description("description")
            .build();
    private final ItemRequestDto itemRequestDto = ItemRequestDto.builder()
            .id(1L)
            .description("description")
            .requester(userDto)
            .items(new ArrayList<>())
            .build();

    @Test
    void createRequestWhenUserIsExistThenReturnedExpectedRequest() {
        Mockito.when(userService.findUserById(anyLong()))
                .thenReturn(userDto);

        Mockito.when(requestRepository.save(any()))
                .thenReturn(itemRequest);

        Assertions.assertEquals(requestService.create(itemRequestDto, 1L), itemRequestDto);
    }

    @Test
    void createRequestWhenUserIsNotExistThenReturnedNotFoundException() {
        Mockito.when(userService.findUserById(anyLong()))
                .thenThrow(new NotFoundException(String.format("User with ID = %d not found.", 1L)));

        Exception e = assertThrows(NotFoundException.class,
                () -> requestService.create(itemRequestDto, 100L));

        assertEquals(e.getMessage(), String.format("User with ID = %d not found.", 1L));
    }

    @Test
    void findByIdWhenRequestIsValidThenReturnedExpectedRequest() {
        Mockito.when(userService.findUserById(anyLong()))
                .thenReturn(userDto);
        Mockito.when(requestRepository.findById(any()))
                .thenReturn(Optional.ofNullable(itemRequest));
        Mockito.when(itemRepository.findAllByRequest(any()))
                .thenReturn(new ArrayList<>());

        Assertions.assertEquals(requestService.findById(1L, 1L), itemRequestDto);
    }

    @Test
    void findByIdWhenRequestIsNotExistThenReturnedNotFoundException() {
        Mockito.when(requestRepository.findById(anyLong()))
                .thenThrow(new NotFoundException(String.format("Request with ID = %d not found.", 1L)));

        Exception e = assertThrows(NotFoundException.class,
                () -> requestService.findById(1L, 1L));

        assertEquals(e.getMessage(), String.format("Request with ID = %d not found.", 1L));
    }

    @Test
    void findAllRequestsWhenParamsIsExistThenReturnedExpectedListRequests() {
        Mockito.when(userService.findUserById(anyLong()))
                .thenReturn(userDto);
        Mockito.when(itemRepository.findAllByRequest(any()))
                .thenReturn(new ArrayList<>());
        Mockito.when(requestRepository.findByRequesterIdIsNot(anyLong(), any()))
                .thenReturn(List.of(itemRequest));

        assertEquals(requestService.findRequests(1L, 1, 1), List.of(itemRequestDto));
    }

    @Test
    void findAllRequestsWhenUserIsNotExistThenReturnedNotFoundException() {
        Mockito.when(userService.findUserById(anyLong()))
                .thenThrow(new NotFoundException(String.format("User with ID = %d not found.", 1L)));

        Exception e = assertThrows(NotFoundException.class,
                () -> requestService.findRequests(1L, 1, 1));

        assertEquals(e.getMessage(), String.format("User with ID = %d not found.", 1L));
    }

    @Test
    void findAllUserRequestsWhenUserIsNotExistThenReturnedNotFoundException() {
        Mockito.when(userService.findUserById(anyLong()))
                .thenThrow(new NotFoundException(String.format("User with ID = %d not found.", 1L)));

        Exception e = assertThrows(NotFoundException.class,
                () -> requestService.findUserRequests(1L));

        assertEquals(e.getMessage(), String.format("User with ID = %d not found.", 1L));
    }
}