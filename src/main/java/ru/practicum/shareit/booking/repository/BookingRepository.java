package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByItemIdOrderByStartAsc(Long itemId);

    List<Booking> findAllByItemIdOrderByStartDesc(Long itemId);

    Page<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId, Pageable pageable);

    Page<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status, Pageable pageable);

    Page<Booking> findAllByItemOwnerIdOrderByStartDesc(Long ownerId, Pageable pageable);

    Page<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(Long ownerId, BookingStatus status, Pageable pageable);

    Optional<Booking> findByIdAndItemOwnerId(Long id, Long ownerId);

    @Query("select b" +
            " from Booking as b " +
            "where b.item.owner.id = ?1 and " +
            "b.start > ?2 and " +
            "b.end > ?2 " +
            "order by b.start desc")
    Page<Booking> findByOwnerAndDatesFuture(Long ownerId, LocalDateTime currentDate, Pageable pageable);

    @Query("select b" +
            " from Booking as b " +
            "where b.item.owner.id = ?1 and " +
            "b.start < ?2 and " +
            "b.end > ?2 " +
            "order by b.start desc")
    Page<Booking> findByOwnerAndDatesCurrent(Long ownerId, LocalDateTime currentDate, Pageable pageable);

    @Query("select b " +
            "from Booking as b " +
            "where b.item.owner.id = ?1 and " +
            "b.start < ?2 and " +
            "b.end < ?2 " +
            "order by b.start desc")
    Page<Booking> findByOwnerAndDatesPast(Long ownerId, LocalDateTime currentDate, Pageable pageable);

    @Query("select b from Booking b " +
            "where b.booker.id = ?1 " +
            "order by b.id desc")
    List<Booking> findAllByBookerId(Long bookerId);

    @Query("select b " +
            "from Booking as b " +
            "where b.status <> 'REJECTED' and " +
            "b.booker.id = ?1 and " +
            "b.start > ?2 and " +
            "b.end > ?2 " +
            "order by b.start desc")
    Page<Booking> findByBookerAndDatesFuture(Long bookerId, LocalDateTime currentDate, Pageable pageable);

    @Query("select b " +
            "from Booking as b " +
            "where b.booker.id = ?1 and " +
            "b.start < ?2 and " +
            "b.end > ?2 " +
            "order by b.start desc")
    Page<Booking> findByBookerAndDatesCurrent(Long bookerId, LocalDateTime currentDate, Pageable pageable);

    @Query("select b " +
            "from Booking as b " +
            "where b.booker.id = ?1 and " +
            "b.start < ?2 and " +
            "b.end < ?2 " +
            "order by b.start desc")
    Page<Booking> findByBookerAndDatesPast(Long bookerId, LocalDateTime currentDate, Pageable pageable);

    @Query("select b " +
            "from Booking as b " +
            "where b.id = ?1 " +
            "and (b.item.owner.id = ?2 or b.booker.id = ?2)")
    Optional<Booking> findByIdAndBookerOrOwner(Long bookingId, Long userId);
}