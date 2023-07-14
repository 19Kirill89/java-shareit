package practic.shareit.booking.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import practic.shareit.booking.dto.OutputBookingDto;
import practic.shareit.booking.dto.ShortItemBookingDto;
import practic.shareit.booking.model.Booking;
import practic.shareit.item.mapper.ItemMapper;
import practic.shareit.user.mapper.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class BookingMapper {

    public static OutputBookingDto toBookingDto(Booking booking) {
        return OutputBookingDto.builder()
                .id(booking.getId())
                .booker(UserMapper.toUserDto(booking.getBooker()))
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(ItemMapper.toItemDto(booking.getItem()))
                .status(booking.getStatus())
                .build();
    }

    public static List<OutputBookingDto> toBookingDto(List<Booking> bookings) {
        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    public static ShortItemBookingDto toItemBookingDto(Booking booking) {
        return ShortItemBookingDto.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .build();
    }
}