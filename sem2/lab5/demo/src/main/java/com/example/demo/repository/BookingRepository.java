package com.example.demo.repository;

import com.example.demo.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByClientId(Long clientId);
    List<Booking> findByTourId(Long tourId);
    List<Booking> findByStatus(Booking.BookingStatus status);
    List<Booking> findByBookingDateBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT b FROM Booking b WHERE b.client.id = :clientId AND b.tour.id = :tourId")
    List<Booking> findByClientAndTour(@Param("clientId") Long clientId, @Param("tourId") Long tourId);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.tour.id = :tourId")
    Long countByTourId(@Param("tourId") Long tourId);
}
