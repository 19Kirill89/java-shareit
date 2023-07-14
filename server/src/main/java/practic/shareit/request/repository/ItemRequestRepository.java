package practic.shareit.request.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import practic.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findByRequesterIdOrderByCreatedDesc(Long userId);

    List<ItemRequest> findByRequesterIdIsNot(Long userId, Pageable page);
}