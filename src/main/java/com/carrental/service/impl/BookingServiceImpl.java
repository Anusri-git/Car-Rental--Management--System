package com.carrental.service.impl;

import com.carrental.dto.BookingRequest;
import com.carrental.dto.DashboardStatsDto;
import com.carrental.entity.Booking;
import com.carrental.entity.Car;
import com.carrental.entity.User;
import com.carrental.enums.BookingStatus;
import com.carrental.exception.BookingConflictException;
import com.carrental.exception.InvalidBookingDateException;
import com.carrental.exception.ResourceNotFoundException;
import com.carrental.exception.UnauthorizedException;
import com.carrental.repository.BookingRepository;
import com.carrental.repository.CarRepository;
import com.carrental.repository.UserRepository;
import com.carrental.service.BookingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service implementation for car rental bookings.
 * Handles validation, pricing calculation, conflict prevention, and returns.
 */
@Service
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final CarRepository carRepository;
    private final UserRepository userRepository;

    public BookingServiceImpl(BookingRepository bookingRepository, 
                              CarRepository carRepository, 
                              UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.carRepository = carRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Booking createBooking(Long userId, BookingRequest request) {
        try {
            // 1. Fetch User
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

            // 2. Fetch Car
            Car car = carRepository.findById(request.getCarId())
                    .orElseThrow(() -> new ResourceNotFoundException("Car not found with id: " + request.getCarId()));

            // 3. Validate Dates
            LocalDate today = LocalDate.now();
            if (request.getPickupDate().isBefore(today)) {
                throw new InvalidBookingDateException("Pickup date cannot be in the past (" + request.getPickupDate() + ")");
            }

            if (request.getReturnDate().isBefore(request.getPickupDate())) {
                throw new InvalidBookingDateException("Return date cannot be earlier than pickup date (" + request.getReturnDate() + ")");
            }

            // 4. Overlap & Conflict Check
            List<Booking> conflicts = bookingRepository.findOverlappingBookings(
                    car.getId(), request.getPickupDate(), request.getReturnDate()
            );

            if (!conflicts.isEmpty()) {
                throw new BookingConflictException("The selected car (" + car.getFullName() + 
                        ") is already reserved between " + request.getPickupDate() + " and " + request.getReturnDate() + ".");
            }

            // 5. Calculate Rental Duration (same day counts as 1 day)
            long days = ChronoUnit.DAYS.between(request.getPickupDate(), request.getReturnDate());
            if (days == 0) {
                days = 1;
            }

            // 6. Calculate Pricing on Server (Secure - never trust client pricing)
            double dailyRate = car.getDailyPrice();
            double totalPrice = Math.round(days * dailyRate * 100.0) / 100.0;

            // 7. Generate Unique Booking Number
            String datePrefix = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String randomSuffix = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
            String bookingNumber = "BK-" + datePrefix + "-" + randomSuffix;

            // 8. Create and Persist Booking
            Booking booking = new Booking(
                    bookingNumber,
                    user,
                    car,
                    request.getPickupDate(),
                    request.getReturnDate(),
                    days,
                    dailyRate,
                    totalPrice,
                    BookingStatus.CONFIRMED
            );

            if (request.getNotes() != null) {
                booking.setReturnNotes(request.getNotes());
            }

            // Update car status if pickup is today
            if (request.getPickupDate().isEqual(today)) {
                car.setAvailable(false);
                carRepository.save(car);
            }

            return bookingRepository.save(booking);

        } catch (InvalidBookingDateException | BookingConflictException | ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error processing booking: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getUserBookings(Long userId) {
        List<Booking> list = bookingRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return new ArrayList<>(list);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getAllBookings() {
        List<Booking> list = bookingRepository.findAllByOrderByCreatedAtDesc();
        return new ArrayList<>(list);
    }

    @Override
    @Transactional(readOnly = true)
    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + id));
    }

    @Override
    public Booking returnCar(Long bookingId, Long userId, String notes, boolean isAdmin) {
        Booking booking = getBookingById(bookingId);

        // Security check: Only the booking owner or an admin can return
        if (!isAdmin && !booking.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to return this booking.");
        }

        if (booking.getStatus() == BookingStatus.COMPLETED) {
            throw new IllegalArgumentException("This booking is already marked as returned/completed.");
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalArgumentException("Cannot return a cancelled booking.");
        }

        // Update booking
        booking.setStatus(BookingStatus.COMPLETED);
        booking.setActualReturnDate(LocalDate.now());
        if (notes != null && !notes.trim().isEmpty()) {
            booking.setReturnNotes(notes.trim());
        }

        // Return the car to available inventory
        Car car = booking.getCar();
        car.setAvailable(true);
        carRepository.save(car);

        return bookingRepository.save(booking);
    }

    @Override
    public Booking cancelBooking(Long bookingId, Long userId, boolean isAdmin) {
        Booking booking = getBookingById(bookingId);

        if (!isAdmin && !booking.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to cancel this booking.");
        }

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new IllegalArgumentException("Only confirmed bookings can be cancelled.");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        booking.setReturnNotes("Booking cancelled by " + (isAdmin ? "Administrator" : "Customer"));

        // Release car back to available
        Car car = booking.getCar();
        car.setAvailable(true);
        carRepository.save(car);

        return bookingRepository.save(booking);
    }

    @Override
    public Booking updateBookingStatus(Long bookingId, BookingStatus status) {
        Booking booking = getBookingById(bookingId);
        booking.setStatus(status);

        Car car = booking.getCar();
        if (status == BookingStatus.COMPLETED || status == BookingStatus.CANCELLED) {
            car.setAvailable(true);
            carRepository.save(car);
        } else if (status == BookingStatus.CONFIRMED) {
            car.setAvailable(false);
            carRepository.save(car);
        }

        return bookingRepository.save(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardStatsDto getDashboardStats() {
        DashboardStatsDto stats = new DashboardStatsDto();
        stats.setTotalCars(carRepository.count());
        stats.setAvailableCars(carRepository.countByAvailableTrue());
        stats.setRentedCars(carRepository.countByAvailableFalse());
        stats.setTotalBookings(bookingRepository.count());
        stats.setActiveBookings(bookingRepository.countByStatus(BookingStatus.CONFIRMED));
        stats.setCompletedBookings(bookingRepository.countByStatus(BookingStatus.COMPLETED));
        stats.setTotalCustomers(userRepository.countByRole(com.carrental.enums.Role.ROLE_USER));
        
        Double totalRev = bookingRepository.calculateTotalRevenue();
        stats.setTotalRevenue(totalRev != null ? totalRev : 0.0);

        return stats;
    }
}
