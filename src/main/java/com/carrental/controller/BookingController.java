package com.carrental.controller;

import com.carrental.dto.ApiResponse;
import com.carrental.dto.AuthResponse;
import com.carrental.dto.BookingRequest;
import com.carrental.entity.Booking;
import com.carrental.enums.Role;
import com.carrental.exception.UnauthorizedException;
import com.carrental.service.BookingService;
import com.carrental.service.ReportService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final ReportService reportService;

    public BookingController(BookingService bookingService, ReportService reportService) {
        this.bookingService = bookingService;
        this.reportService = reportService;
    }

    private AuthResponse getAuthenticatedUser(HttpSession session) {
        AuthResponse user = (AuthResponse) session.getAttribute(AuthController.SESSION_USER_KEY);
        if (user == null) {
            throw new UnauthorizedException("You must be logged in to perform this operation.");
        }
        return user;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Booking>> createBooking(@Valid @RequestBody BookingRequest request,
                                                              HttpSession session) {
        AuthResponse user = getAuthenticatedUser(session);
        Booking booking = bookingService.createBooking(user.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Booking confirmed successfully!", booking));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<Booking>>> getMyBookings(HttpSession session) {
        AuthResponse user = getAuthenticatedUser(session);
        List<Booking> bookings = bookingService.getUserBookings(user.getId());
        return ResponseEntity.ok(ApiResponse.success("User bookings retrieved", bookings));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Booking>> getBookingById(@PathVariable Long id, HttpSession session) {
        AuthResponse user = getAuthenticatedUser(session);
        Booking booking = bookingService.getBookingById(id);
        
        // Authorization check: must be owner or admin
        if (user.getRole() != Role.ROLE_ADMIN && !booking.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("Access denied to this booking details.");
        }
        return ResponseEntity.ok(ApiResponse.success("Booking found", booking));
    }

    @PutMapping("/{id}/return")
    public ResponseEntity<ApiResponse<Booking>> returnCar(@PathVariable Long id, 
                                                         @RequestBody(required = false) Map<String, String> body,
                                                         HttpSession session) {
        AuthResponse user = getAuthenticatedUser(session);
        String notes = body != null ? body.get("notes") : null;
        boolean isAdmin = user.getRole() == Role.ROLE_ADMIN;

        Booking updated = bookingService.returnCar(id, user.getId(), notes, isAdmin);
        return ResponseEntity.ok(ApiResponse.success("Car returned successfully!", updated));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<Booking>> cancelBooking(@PathVariable Long id, HttpSession session) {
        AuthResponse user = getAuthenticatedUser(session);
        boolean isAdmin = user.getRole() == Role.ROLE_ADMIN;

        Booking updated = bookingService.cancelBooking(id, user.getId(), isAdmin);
        return ResponseEntity.ok(ApiResponse.success("Booking cancelled successfully", updated));
    }

    @GetMapping("/{id}/receipt")
    public ResponseEntity<byte[]> downloadReceipt(@PathVariable Long id, HttpSession session) throws IOException {
        AuthResponse user = getAuthenticatedUser(session);
        Booking booking = bookingService.getBookingById(id);

        if (user.getRole() != Role.ROLE_ADMIN && !booking.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("Access denied to download this receipt.");
        }

        byte[] receiptData = reportService.generateBookingReceipt(booking);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"Receipt-" + booking.getBookingNumber() + ".txt\"")
                .contentType(MediaType.TEXT_PLAIN)
                .body(receiptData);
    }
}
