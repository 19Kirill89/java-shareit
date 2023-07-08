package practic.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import practic.shareit.booking.controller.BookingController;
import practic.shareit.booking.dto.InputBookingDto;
import practic.shareit.booking.dto.OutputBookingDto;
import practic.shareit.booking.service.BookingService;
import practic.shareit.exception.NotFoundException;
import practic.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    private final ItemDto itemDto = ItemDto.builder()
            .name("testItem")
            .description("testDescription")
            .available(true)
            .build();
    private final InputBookingDto inputBookingDto = InputBookingDto.builder()
            .start(LocalDateTime.of(2023, 6, 1, 15, 0, 0))
            .end(LocalDateTime.of(2023, 6, 10, 15, 0, 0))
            .itemId(1L).build();
    private final OutputBookingDto outputBookingDto = OutputBookingDto.builder()
            .start(LocalDateTime.of(2023, 6, 1, 15, 0, 0))
            .end(LocalDateTime.of(2023, 6, 10, 15, 0, 0))
            .item(itemDto)
            .build();

    @Test
    void createBookingWhenStartIsNotValidThenReturnedStatusIsBadRequest() throws Exception {
        InputBookingDto badInputBookingDto = InputBookingDto.builder()
                .start(LocalDateTime.of(1000, 5, 10, 13, 0, 0))
                .end(LocalDateTime.of(2023, 5, 20, 13, 0, 0))
                .itemId(1L)
                .build();

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(badInputBookingDto)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(bookingService, never()).create(badInputBookingDto, 1L);
    }

    @Test
    void findBookingByIdWhenAllParamsIsValidThenReturnedStatusIsOk() throws Exception {
        Mockito.when(bookingService.findBookingById(anyLong(), anyLong()))
                .thenReturn(outputBookingDto);

        mvc.perform(get("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(inputBookingDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(bookingService).findBookingById(1L, 1L);
    }

    @Test
    void findBookingByIdWhenBookingIdNotFoundThenReturnedStatusIsNotFound() throws Exception {
        Mockito.when(bookingService.findBookingById(anyLong(), anyLong()))
                .thenThrow(new NotFoundException(String.format("Booking with ID = %d not found.", 100L)));

        mvc.perform(get("/bookings/{bookingId}", 100L)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(inputBookingDto)))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    void findAllByUserIdWhenAllParamsIsValidThenReturnedStatusIsOk() throws Exception {
        Mockito.when(bookingService.findBookingsByUser(any(), anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(outputBookingDto));

        String result = mvc.perform(get("/bookings/?state=ALL")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(result, mapper.writeValueAsString(List.of(outputBookingDto)));
    }

    @Test
    void findAllByOwnerIdWhenAllParamsIsValidThenReturnedStatusIsOk() throws Exception {
        Mockito.when(bookingService.findBookingsByOwner(any(), anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(outputBookingDto));

        String result = mvc.perform(get("/bookings/owner?state=ALL")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(result, mapper.writeValueAsString(List.of(outputBookingDto)));
    }

    @Test
    void approveBookingWhenAllParamsIsValidThenReturnedStatusIsOk() throws Exception {
        Mockito.when(bookingService.approve(anyLong(), anyInt(), anyBoolean()))
                .thenReturn(outputBookingDto);

        mvc.perform(get("/bookings/1?approved=true")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }
}