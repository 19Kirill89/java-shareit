package practic.shareit.request.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import practic.shareit.request.model.ItemRequest;
import practic.shareit.item.mapper.ItemMapper;
import practic.shareit.request.dto.ItemRequestDto;
import practic.shareit.user.mapper.UserMapper;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class ItemRequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest request) {
        return ItemRequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .requester(UserMapper.toUserDto(request.getRequester()))
                .items(request.getItems() != null ? request.getItems()
                        .stream()
                        .map(ItemMapper::toItemDto)
                        .collect(Collectors.toList())
                        : new ArrayList<>())
                .build();
    }

    public static ItemRequest toItemRequest(ItemRequestDto requestDto) {
        return ItemRequest.builder()
                .id(requestDto.getId())
                .description(requestDto.getDescription())
                .requester(UserMapper.toUser(requestDto.getRequester()))
                .created(requestDto.getCreated())
                .build();
    }
}