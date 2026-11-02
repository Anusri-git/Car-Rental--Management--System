package com.carrental.controller;

import com.carrental.dto.ApiResponse;
import com.carrental.dto.AuthResponse;
import com.carrental.dto.CarDto;
import com.carrental.dto.DashboardStatsDto;
import com.carrental.entity.Booking;
import com.carrental.entity.Car;
import com.carrental.entity.User;
import com.carrental.enums.BookingStatus;
import com.carrental.enums.Role;
import com.carrental.exception.UnauthorizedException;
import com.carrental.service.BookingService;
import com.carrental.service.CarService;
import com.carrental.service.ReportService;
import com.carrental.service.UserService;
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
@RequestMapping("/api/admin")
public class AdminController {

    private final CarService carService;
    private final BookingService bookingService;
    private final UserService userService;
    private final ReportService reportService;

    public AdminController(CarService carService, 
                           BookingService bookingService, 
                           UserService userService, 
                           ReportService reportService) {
        this.carService = carService;
        this.bookingService = bookingService;
        this.userService = userService;
        this.reportService = reportService;
    }

    private void requireAdminRole(HttpSession session) {
        AuthResponse user = (AuthResponse) session.getAttribute(AuthController.SESSION_USER_KEY);
        if (user == null || user.getRole() != Role.ROLE_ADMIN) {
            throw new UnauthorizedException("Administrative privileges required for this action.");
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<DashboardStatsDto>> getStats(HttpSession session) {
        requireAdminRole(session);
        DashboardStatsDto stats = bookingService.getDashboardStats();
        return ResponseEntity.ok(ApiResponse.success("Dashboard metrics retrieved", stats));
    }

    @GetMapping("/cars")
    public ResponseEntity<ApiResponse<List<Car>>> getAllCars(HttpSession session) {
        requireAdminRole(session);
        List<Car> cars = carService.findAll();
        return ResponseEntity.ok(ApiResponse.success("All cars inventory", cars));
    }

    @PostMapping("/cars")
    public ResponseEntity<ApiResponse<Car>> addCar(@Valid @RequestBody CarDto carDto, HttpSession session) {
        requireAdminRole(session);
        Car created = carService.createCar(carDto);
        return ResponseEntity.ok(ApiResponse.success("Car added to inventory successfully", created));
    }

    @PutMapping("/cars/{id}")
    public ResponseEntity<ApiResponse<Car>> updateCar(@PathVariable Long id, 
                                                      @Valid @RequestBody CarDto carDto, 
                                                      HttpSession session) {
        requireAdminRole(session);
        Car updated = carService.updateCar(id, carDto);
        return ResponseEntity.ok(ApiResponse.success("Car updated successfully", updated));
    }

    @DeleteMapping("/cars/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCar(@PathVariable Long id, HttpSession session) {
        requireAdminRole(session);
        carService.deleteCar(id);
        return ResponseEntity.ok(ApiResponse.success("Car deleted from inventory"));
    }

    @GetMapping("/bookings")
    public ResponseEntity<ApiResponse<List<Booking>>> getAllBookings(HttpSession session) {
        requireAdminRole(session);
        List<Booking> bookings = bookingService.getAllBookings();
        return ResponseEntity.ok(ApiResponse.success("All bookings retrieved", bookings));
    }

    @PutMapping("/bookings/{id}/status")
    public ResponseEntity<ApiResponse<Booking>> updateBookingStatus(@PathVariable Long id, 
                                                                    @RequestBody Map<String, String> body, 
                                                                    HttpSession session) {
        requireAdminRole(session);
        String statusStr = body.get("status");
        if (statusStr == null) {
            throw new IllegalArgumentException("Status is required");
        }
        BookingStatus status = BookingStatus.valueOf(statusStr.toUpperCase());
        Booking updated = bookingService.updateBookingStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("Booking status updated to " + status, updated));
    }

    @GetMapping("/customers")
    public ResponseEntity<ApiResponse<List<User>>> getAllCustomers(HttpSession session) {
        requireAdminRole(session);
        List<User> customers = userService.findAllCustomers();
        return ResponseEntity.ok(ApiResponse.success("Customer list", customers));
    }

    @GetMapping("/bookings/export")
    public ResponseEntity<byte[]> exportBookingsReport(HttpSession session) throws IOException {
        requireAdminRole(session);
        List<Booking> bookings = bookingService.getAllBookings();
        byte[] csvData = reportService.generateBookingsCsvReport(bookings);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"CarRental_Bookings_Report.csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvData);
    }
}
