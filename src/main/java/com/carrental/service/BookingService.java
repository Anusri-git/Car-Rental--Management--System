package com.carrental.service;

import com.carrental.dto.BookingRequest;
import com.carrental.dto.DashboardStatsDto;
import com.carrental.entity.Booking;
import com.carrental.enums.BookingStatus;

import java.util.List;

public interface BookingService {
    Booking createBooking(Long userId, BookingRequest request);
    List<Booking> getUserBookings(Long userId);
    List<Booking> getAllBookings();
    Booking getBookingById(Long id);
    Booking returnCar(Long bookingId, Long userId, String notes, boolean isAdmin);
    Booking cancelBooking(Long bookingId, Long userId, boolean isAdmin);
    Booking updateBookingStatus(Long bookingId, BookingStatus status);
    DashboardStatsDto getDashboardStats();
}
