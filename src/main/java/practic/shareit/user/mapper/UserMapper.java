package practic.shareit.user.mapper;

import org.springframework.stereotype.Component;
import practic.shareit.user.dto.UserDto;
import practic.shareit.user.model.User;

@Component
public class UserMapper {
    public static User toUser(UserDto userDto) {
        return User.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }

    public static UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}