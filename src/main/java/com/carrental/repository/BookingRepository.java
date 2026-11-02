package com.carrental.repository;

import com.carrental.entity.Booking;
import com.carrental.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Booking> findAllByOrderByCreatedAtDesc();

    List<Booking> findByStatus(BookingStatus status);

    long countByStatus(BookingStatus status);

    // Overlapping confirmed bookings detection query
    @Query("SELECT b FROM Booking b WHERE b.car.id = :carId " +
           "AND b.status = com.carrental.enums.BookingStatus.CONFIRMED " +
           "AND b.pickupDate <= :returnDate AND b.returnDate >= :pickupDate")
    List<Booking> findOverlappingBookings(@Param("carId") Long carId,
                                         @Param("pickupDate") LocalDate pickupDate,
                                         @Param("returnDate") LocalDate returnDate);

    @Query("SELECT COALESCE(SUM(b.totalPrice), 0.0) FROM Booking b WHERE b.status != com.carrental.enums.BookingStatus.CANCELLED")
    Double calculateTotalRevenue();
}
