package shareit.user.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shareit.exception.NotFoundException;
import shareit.user.dto.UserDto;
import shareit.user.mapper.UserMapper;
import shareit.user.model.User;
import shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public UserDto create(UserDto userDto) {
        return userMapper.toUserDto(userRepository.save(userMapper.toUser(userDto)));
    }

    @Transactional
    public UserDto findUserById(Long userId) {
        return userMapper.toUserDto(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with ID = %d not found.", userId))));
    }

    @Transactional
    public List<UserDto> findAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserDto save(UserDto userDto, Long userId) {
        User user = userMapper.toUser(findUserById(userId));
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        return userMapper.toUserDto(userRepository.save(user));
    }

    @Transactional
    public void delete(Long userId) {
        userRepository.deleteById(userId);
    }
}