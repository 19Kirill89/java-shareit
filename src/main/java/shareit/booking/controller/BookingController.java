package shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import shareit.booking.dto.InputBookingDto;
import shareit.booking.dto.OutputBookingDto;
import shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
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
        log.info("GET- запрос: '/bookings' для получения booking with ID = {}", bookingId);
        return bookingService.findBookingById(bookingId, userId);
    }

    @GetMapping
    public List<OutputBookingDto> findAllByUserId(@RequestHeader(OWNER_ID_HEADER) Long userId,
                                                  @RequestParam(defaultValue = "ALL") String state) {
        log.info("GET-запрос: '/bookings' для получения всех booking пользователя с ID = {}", userId);
        return bookingService.findAllBookingsByUser(state, userId);
    }

    @GetMapping("/owner")
    public List<OutputBookingDto> findAllByOwnerId(@RequestHeader(OWNER_ID_HEADER) Long userId,
                                                   @RequestParam(defaultValue = "ALL") String state) {
        log.info("GET-запрос: '/bookings'чтобы получить все бронирования владельца ID = {}", userId);
        return bookingService.findAllBookingsByOwner(state, userId);
    }

    @PatchMapping("/{bookingId}")
    public OutputBookingDto save(@RequestHeader(OWNER_ID_HEADER) Long userId,
                                 @PathVariable Long bookingId,
                                 @RequestParam Boolean approved) {
        log.info("PATCH-запрос: '/bookings' обновить booking с ID = {}", bookingId);
        return bookingService.approve(bookingId, userId, approved);
    }
}