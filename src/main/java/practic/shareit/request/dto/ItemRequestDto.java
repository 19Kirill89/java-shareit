package practic.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import practic.shareit.item.dto.ItemDto;
import practic.shareit.user.dto.UserDto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
public class ItemRequestDto {
    private Long id;

    @NotBlank
    private String description;
    private UserDto requester;
    private LocalDateTime created;
    private List<ItemDto> items;
}