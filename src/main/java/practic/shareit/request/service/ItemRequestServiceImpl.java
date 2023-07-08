package practic.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import practic.shareit.item.dto.ItemDto;
import practic.shareit.item.mapper.ItemMapper;
import practic.shareit.item.model.Item;
import practic.shareit.request.repository.ItemRequestRepository;
import practic.shareit.exception.NotFoundException;
import practic.shareit.item.repository.ItemRepository;
import practic.shareit.request.dto.ItemRequestDto;
import practic.shareit.request.mapper.ItemRequestMapper;
import practic.shareit.request.model.ItemRequest;
import practic.shareit.user.mapper.UserMapper;
import practic.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final UserServiceImpl userService;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto create(ItemRequestDto itemRequestDto, Long userId) {
        ItemRequest itemRequest = ItemRequest.builder()
                .description(itemRequestDto.getDescription())
                .requester(UserMapper.toUser(userService.findUserById(userId)))
                .created(LocalDateTime.now())
                .build();
        return ItemRequestMapper.toItemRequestDto(requestRepository.save(itemRequest));
    }

    @Override
    public ItemRequestDto findById(Long userId, Long requestId) {
        ItemRequest itemRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format("Request with ID = %d not found.", requestId)));
        itemRequest.setItems(itemRepository.findAllByRequest(itemRequest));
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
        itemRequestDto.setRequester(userService.findUserById(userId));
        return itemRequestDto;
    }

    @Override
    public List<ItemRequestDto> findRequests(Long userId, int from, int size) {
        UserMapper.toUser(userService.findUserById(userId));
        Pageable page = PageRequest.of(from / size, size, Sort.by("created"));
        List<ItemRequestDto> list = new ArrayList<>();
        List<ItemRequest> findAllByRequesterIdIsNot = requestRepository.findByRequesterIdIsNot(userId, page);
        findAllByRequesterIdIsNot.forEach(itemRequest -> {
            itemRequest.setItems(itemRepository.findAllByRequest(itemRequest));
            ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
            list.add(itemRequestDto);
        });
        return list;
    }

    @Override
    public List<ItemRequestDto> findUserRequests(Long userId) {
        userService.findUserById(userId);

        List<ItemRequest> requests = requestRepository.findByRequesterIdOrderByCreatedDesc(userId);
        List<Long> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .toList();
        Map<Long, ItemRequest> requestsMap = requests.stream().collect(Collectors.toMap(ItemRequest::getId, itemRequest -> itemRequest));

        Map<Long, List<ItemDto>> itemsByRequest = itemRepository.findByRequestIdIn(requestIds)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(groupingBy(ItemDto::getRequestId, toList()));
        List<ItemRequestDto> results = new ArrayList<>();
        for (ItemRequest request : requests) {
            List<ItemDto> itemDtos = itemsByRequest.getOrDefault(request.getId(), Collections.emptyList());
            List<Item> items = itemDtos.stream().map(itemDto -> {
                Item item = ItemMapper.toItem(itemDto);
                item.setRequest(requestsMap.get(itemDto.getRequestId()));
                return item;
            }).toList();
            ItemRequestDto info = ItemRequestMapper.toItemRequestDto(request, items);

            results.add(info);
        }

        return results;
    }
}