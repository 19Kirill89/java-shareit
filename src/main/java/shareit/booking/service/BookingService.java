package shareit.booking.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shareit.booking.dto.InputBookingDto;
import shareit.booking.dto.OutputBookingDto;
import shareit.booking.mapper.BookingMapper;
import shareit.booking.model.Booking;
import shareit.booking.model.BookingStatus;
import shareit.booking.repository.BookingRepository;
import shareit.exception.*;
import shareit.item.mapper.ItemMapper;
import shareit.item.model.Item;
import shareit.item.service.ItemService;
import shareit.user.mapper.UserMapper;
import shareit.user.model.User;
import shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;
    private final ItemMapper itemMapper;
    private final UserMapper userMapper;
    private final BookingMapper bookingMapper;

    @Transactional
    public OutputBookingDto create(InputBookingDto bookingDtoShort, long bookerId) {
        if (bookingDtoShort.getEnd().isBefore(bookingDtoShort.getStart()) ||
                bookingDtoShort.getEnd().equals(bookingDtoShort.getStart())) {
            throw new TimeDataException(String
                    .format("некорректное время начала = %s  end = %s",
                            bookingDtoShort.getStart(), bookingDtoShort.getEnd()));
        }
        User booker = userMapper.toUser(userService.findUserById(bookerId));
        Item item = itemMapper.toItem(itemService.findItemById(bookingDtoShort.getItemId(), bookerId));
        if (itemService.findOwnerId(item.getId()) == bookerId) {
            throw new OperationAccessException("Владелец не может быть заказчиком.");
        }
        if (item.getAvailable()) {
            Booking booking = Booking.builder()
                    .start(bookingDtoShort.getStart())
                    .end(bookingDtoShort.getEnd())
                    .item(item)
                    .booker(booker)
                    .status(BookingStatus.WAITING)
                    .build();
            return bookingMapper.toBookingDto(bookingRepository.save(booking));
        } else {
            throw new NotAvailableException("Элемент с  ID = %d недоступен.");
        }
    }

    @Transactional
    public OutputBookingDto findBookingById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("Booking с ID = %d не найден", bookingId)));
        if (booking.getBooker().getId().equals(userId) || booking.getItem().getOwnerId().equals(userId)) {
            return bookingMapper.toBookingDto(booking);
        } else {
            throw new OperationAccessException(String.format("Пользователь с ID = %d не является владельцем и " +
                            "нет доступа к бронированию.",
                    userId));
        }
    }

    @Transactional
    public List<OutputBookingDto> findAllBookingsByUser(String state, Long userId) {
        userService.findUserById(userId);
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case "ALL":
                return bookingMapper.toBookingDto(bookingRepository.findByBookerIdOrderByStartDesc(userId));
            case "CURRENT":
                return bookingMapper.toBookingDto(bookingRepository
                        .findByBookerIdAndEndIsAfterAndStartIsBeforeOrderByStartDesc(userId, now, now));
            case "PAST":
                return bookingMapper.toBookingDto(bookingRepository
                        .findByBookerIdAndEndIsBeforeOrderByStartDesc(userId, now));
            case "FUTURE":
                return bookingMapper.toBookingDto(bookingRepository
                        .findByBookerIdAndStartIsAfterOrderByStartDesc(userId, now));
            case "WAITING":
                return bookingMapper.toBookingDto(bookingRepository
                        .findByBookerIdAndStartIsAfterAndStatusIsOrderByStartDesc(userId, now,
                                BookingStatus.WAITING));
            case "REJECTED":
                return bookingMapper.toBookingDto(bookingRepository
                        .findByBookerIdAndStatusIsOrderByStartDesc(userId, BookingStatus.REJECTED));

        }
        throw new BadRequestException(String.format("Unknown state: %s", state));
    }

    @Transactional
    public List<OutputBookingDto> findAllBookingsByOwner(String state, Long ownerId) {
        userService.findUserById(ownerId);
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case "ALL":
                return bookingMapper.toBookingDto(bookingRepository.findByItemOwnerId(ownerId));
            case "CURRENT":
                return bookingMapper.toBookingDto(bookingRepository.findCurrentBookingsOwner(ownerId, now));
            case "PAST":
                return bookingMapper.toBookingDto(bookingRepository.findPastBookingsOwner(ownerId, now));
            case "FUTURE":
                return bookingMapper.toBookingDto(bookingRepository.findFutureBookingsOwner(ownerId, now));
            case "WAITING":
                return bookingMapper.toBookingDto(bookingRepository
                        .findWaitingBookingsOwner(ownerId, now, BookingStatus.WAITING));
            case "REJECTED":
                return bookingMapper.toBookingDto(bookingRepository
                        .findRejectedBookingsOwner(ownerId, BookingStatus.REJECTED));
        }
        throw new BadRequestException(String.format("Unknown state: %s", state));
    }

    @Transactional
    public OutputBookingDto approve(long bookingId, long userId, Boolean approve) {
        OutputBookingDto booking = findBookingById(bookingId, userId);
        Long ownerId = itemService.findOwnerId(booking.getItem().getId());
        if (ownerId.equals(userId)
                && booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new AlreadyExistsException("Решение о бронировании уже принято.");
        }
        if (!ownerId.equals(userId)) {
            throw new OperationAccessException(String.format("Пользователь с ID = %d не владелец " +
                    "и нет доступа к booking.", userId));
        }
        if (approve) {
            booking.setStatus(BookingStatus.APPROVED);
            bookingRepository.save(BookingStatus.APPROVED, bookingId);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
            bookingRepository.save(BookingStatus.REJECTED, bookingId);
        }
        return booking;
    }
}