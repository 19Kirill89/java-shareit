package practic.shareit.item.model;

import lombok.*;
import practic.shareit.booking.model.Booking;
import practic.shareit.item.comment.dto.CommentDto;
import practic.shareit.request.model.ItemRequest;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

@Entity
@Table(name = "items")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "is_available", nullable = false)
    private Boolean available;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @Transient
    private Booking lastBooking;

    @Transient
    private Booking nextBooking;

    @Transient
    private List<CommentDto> comments;

    @ManyToOne
    @JoinColumn(name = "request_id")
    private ItemRequest request;
}