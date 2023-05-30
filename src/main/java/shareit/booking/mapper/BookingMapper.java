package shareit.booking.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import shareit.booking.dto.OutputBookingDto;
import shareit.booking.dto.ShortItemBookingDto;
import shareit.booking.model.Booking;
import shareit.item.mapper.ItemMapper;
import shareit.user.mapper.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class BookingMapper {
    private final ItemMapper itemMapper;
    private final UserMapper userMapper;

    public OutputBookingDto toBookingDto(Booking booking) {
        return OutputBookingDto.builder()
                .id(booking.getId())
                .booker(userMapper.toUserDto(booking.getBooker()))
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(itemMapper.toItemDto(booking.getItem()))
                .status(booking.getStatus())
                .build();
    }

    public List<OutputBookingDto> toBookingDto(List<Booking> bookings) {
        return bookings.stream()
                .map(this::toBookingDto)
                .collect(Collectors.toList());
    }

    public ShortItemBookingDto toItemBookingDto(Booking booking) {
        return ShortItemBookingDto.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .build();
    }
}