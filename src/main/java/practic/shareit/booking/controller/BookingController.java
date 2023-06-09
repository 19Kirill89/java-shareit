package practic.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import practic.shareit.booking.dto.InputBookingDto;
import practic.shareit.booking.dto.OutputBookingDto;
import practic.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private static final String OWNER_ID_HEADER = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @PostMapping
    public OutputBookingDto create(@RequestHeader(OWNER_ID_HEADER) long userId,
                                   @Valid @RequestBody InputBookingDto bookingDtoShort) {
        log.info("POST-запрос: '/bookings' чтобы добавить бронирование пользователем с помощью ID = {}", userId);
        return bookingService.create(bookingDtoShort, userId);
    }

    @GetMapping("/{bookingId}")
    public OutputBookingDto findById(@RequestHeader(OWNER_ID_HEADER) Long userId, @PathVariable Long bookingId) {
        log.info("GET- запрос: '/bookings' для получения booking c ID = {}", bookingId);
        return bookingService.findBookingById(bookingId, userId);
    }
    @GetMapping
    public List<OutputBookingDto> findByUserId(@RequestHeader(OWNER_ID_HEADER) Long userId,
                                               @RequestParam(defaultValue = "ALL") String state,
                                               @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                               @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("GET-запрос: '/bookings' для получения всех booking пользователя с ID = {}", userId);
        return bookingService.findBookingsByUser(state, userId, from, size);
    }

    @GetMapping("/owner")
    public List<OutputBookingDto> findByOwnerId(@RequestHeader(OWNER_ID_HEADER) Long userId,
                                                @RequestParam(defaultValue = "ALL") String state,
                                                @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("GET-запрос: '/bookings'чтобы получить все бронирования владельца ID = {}", userId);
        return bookingService.findBookingsByOwner(state, userId, from, size);
    }

    @PatchMapping("/{bookingId}")
    public OutputBookingDto save(@RequestHeader(OWNER_ID_HEADER) Long userId,
                                 @PathVariable Long bookingId,
                                 @RequestParam Boolean approved) {
        log.info("PATCH-запрос: '/bookings' обновить booking с ID = {}", bookingId);
        return bookingService.approve(bookingId, userId, approved);
    }
}