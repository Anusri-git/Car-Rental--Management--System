package com.carrental.entity;

import com.carrental.enums.BookingStatus;
import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Booking entity encapsulates rental transactions.
 * Demonstrates OOP Inheritance (extends BaseEntity) and relationships.
 */
@Entity
@Table(name = "bookings")
public class Booking extends BaseEntity {

    @Column(name = "booking_number", unique = true, nullable = false, length = 50)
    private String bookingNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;

    @Column(name = "pickup_date", nullable = false)
    private LocalDate pickupDate;

    @Column(name = "return_date", nullable = false)
    private LocalDate returnDate;

    @Column(name = "total_days", nullable = false)
    private Long totalDays;

    @Column(name = "daily_rate", nullable = false)
    private Double dailyRate;

    @Column(name = "total_price", nullable = false)
    private Double totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private BookingStatus status;

    @Column(name = "actual_return_date")
    private LocalDate actualReturnDate;

    @Column(name = "return_notes", length = 500)
    private String returnNotes;

    // Constructors
    public Booking() {
        super();
    }

    public Booking(String bookingNumber, User user, Car car, LocalDate pickupDate, 
                   LocalDate returnDate, Long totalDays, Double dailyRate, 
                   Double totalPrice, BookingStatus status) {
        super();
        this.bookingNumber = bookingNumber;
        this.user = user;
        this.car = car;
        this.pickupDate = pickupDate;
        this.returnDate = returnDate;
        this.totalDays = totalDays;
        this.dailyRate = dailyRate;
        this.totalPrice = totalPrice;
        this.status = status;
    }

    // Getters and Setters
    public String getBookingNumber() {
        return bookingNumber;
    }

    public void setBookingNumber(String bookingNumber) {
        this.bookingNumber = bookingNumber;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public LocalDate getPickupDate() {
        return pickupDate;
    }

    public void setPickupDate(LocalDate pickupDate) {
        this.pickupDate = pickupDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public Long getTotalDays() {
        return totalDays;
    }

    public void setTotalDays(Long totalDays) {
        this.totalDays = totalDays;
    }

    public Double getDailyRate() {
        return dailyRate;
    }

    public void setDailyRate(Double dailyRate) {
        this.dailyRate = dailyRate;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public LocalDate getActualReturnDate() {
        return actualReturnDate;
    }

    public void setActualReturnDate(LocalDate actualReturnDate) {
        this.actualReturnDate = actualReturnDate;
    }

    public String getReturnNotes() {
        return returnNotes;
    }

    public void setReturnNotes(String returnNotes) {
        this.returnNotes = returnNotes;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "id=" + getId() +
                ", bookingNumber='" + bookingNumber + '\'' +
                ", user=" + (user != null ? user.getName() : null) +
                ", car=" + (car != null ? car.getFullName() : null) +
                ", pickupDate=" + pickupDate +
                ", returnDate=" + returnDate +
                ", totalDays=" + totalDays +
                ", totalPrice=" + totalPrice +
                ", status=" + status +
                '}';
    }
}
