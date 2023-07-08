package practic.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import practic.shareit.booking.mapper.BookingMapper;
import practic.shareit.booking.model.Booking;
import practic.shareit.booking.model.BookingStatus;
import practic.shareit.booking.repository.BookingRepository;
import practic.shareit.exception.NotAvailableException;
import practic.shareit.item.repository.ItemRepository;
import practic.shareit.request.mapper.ItemRequestMapper;
import practic.shareit.request.service.ItemRequestService;
import practic.shareit.user.mapper.UserMapper;
import practic.shareit.user.model.User;
import practic.shareit.user.service.UserService;
import practic.shareit.exception.NotFoundException;
import practic.shareit.exception.OperationAccessException;
import practic.shareit.item.comment.dto.CommentDto;
import practic.shareit.item.comment.mapper.CommentMapper;
import practic.shareit.item.comment.model.Comment;
import practic.shareit.item.comment.repository.CommentRepository;
import practic.shareit.item.dto.ItemDto;
import practic.shareit.item.mapper.ItemMapper;
import practic.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final CommentRepository commentRepository;
    private final ItemRequestService requestService;

    @Override
    @Transactional
    public ItemDto create(Long userId, ItemDto itemDto) {
        userService.findUserById(userId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwnerId(userId);
        item.setRequest(itemDto.getRequestId() != null ?
                ItemRequestMapper.toItemRequest(requestService.findById(userId, itemDto.getRequestId())) : null);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemDto findItemById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item with ID = %d not found.", itemId)));
        ItemDto result = ItemMapper.toItemDto(item);
        if (Objects.equals(item.getOwnerId(), userId)) {
            updateBookings(result);
        }
        List<Comment> comments = commentRepository.findByItemId(result.getId());
        result.setComments(CommentMapper.toDtoList(comments));
        return result;
    }

    @Override
    @Transactional
    public List<ItemDto> findUserItems(Long userId, Integer from, Integer size) {
        Pageable page = PageRequest.of(from / size, size);
        List<ItemDto> item = itemRepository.findAllByOwnerId(userId, page).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        List<ItemDto> list = new ArrayList<>();
        item.stream().map(this::updateBookings).forEach(i -> {
            CommentMapper.toDtoList(commentRepository.findByItemId(i.getId()));
            list.add(i);
        });
        return list;
    }

    @Override
    @Transactional
    public ItemDto save(ItemDto itemDto, Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item with ID = %d not found.", itemId)));
        userService.findUserById(userId);
        if (!item.getOwnerId().equals(userId)) {
            throw new OperationAccessException(String.format("User with ID = %d is not an owner, update is not available.", userId));
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemDto updateBookings(ItemDto itemDto) {
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = bookingRepository.findBookingsItem(itemDto.getId());
        Booking lastBooking = bookings.stream()
                .filter(obj -> !(obj.getStatus().equals(BookingStatus.REJECTED)))
                .filter(obj -> obj.getStart().isBefore(now))
                .min((obj1, obj2) -> obj2.getStart().compareTo(obj1.getStart())).orElse(null);
        Booking nextBooking = bookings.stream()
                .filter(obj -> !(obj.getStatus().equals(BookingStatus.REJECTED)))
                .filter(obj -> obj.getStart().isAfter(now))
                .min(Comparator.comparing(Booking::getStart)).orElse(null);
        if (lastBooking != null) {
            itemDto.setLastBooking(BookingMapper.toItemBookingDto(lastBooking));
        }
        if (nextBooking != null) {
            itemDto.setNextBooking(BookingMapper.toItemBookingDto(nextBooking));
        }
        return itemDto;
    }

    @Override
    @Transactional
    public void delete(Long itemId) {
        itemRepository.deleteById(itemId);
    }

    @Override
    @Transactional
    public List<ItemDto> search(String text, Integer from, Integer size) {
        Pageable page = PageRequest.of(from / size, size);
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.searchAvailableItems(text, page).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Long findOwnerId(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item with ID = %d not found.", itemId)))
                .getOwnerId();
    }

    @Override
    @Transactional
    public CommentDto addComment(Long itemId, Long userId, CommentDto commentDto) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item with ID = %d not found.", itemId)));
        User user = UserMapper.toUser(userService.findUserById(userId));
        List<Booking> bookings = bookingRepository
                .findByItemIdAndBookerIdAndStatusIsAndEndIsBefore(itemId, userId, BookingStatus.APPROVED, LocalDateTime.now());
        log.info(bookings.toString());
        if (!(!bookings.isEmpty() && bookings.get(0).getStart().isBefore(LocalDateTime.now()))) {
            throw new NotAvailableException(String.format("Booking for user with ID = %d and item with ID = %d not found.", userId, itemId));
        } else {
            Comment comment = CommentMapper.toComment(commentDto);
            comment.setItem(item);
            comment.setAuthor(user);
            comment.setCreated(LocalDateTime.now());
            return CommentMapper.toDto(commentRepository.save(comment));
        }
    }
}