package com.carrental.entity;

import com.carrental.enums.CarCategory;
import jakarta.persistence.*;

/**
 * Car entity represents vehicles available for rental.
 * Demonstrates OOP Inheritance (extends BaseEntity) and Encapsulation.
 */
@Entity
@Table(name = "cars")
public class Car extends BaseEntity {

    @Column(nullable = false, length = 50)
    private String brand;

    @Column(nullable = false, length = 80)
    private String model;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CarCategory category;

    @Column(name = "daily_price", nullable = false)
    private Double dailyPrice;

    @Column(nullable = false)
    private boolean available = true;

    @Column(name = "fuel_type", length = 30)
    private String fuelType = "Petrol";

    @Column(length = 30)
    private String transmission = "Automatic";

    @Column(name = "seating_capacity")
    private Integer seatingCapacity = 5;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "license_plate", unique = true, length = 30)
    private String licensePlate;

    @Column(length = 500)
    private String description;

    // Constructors
    public Car() {
        super();
    }

    public Car(String brand, String model, CarCategory category, Double dailyPrice, 
               boolean available, String fuelType, String transmission, 
               Integer seatingCapacity, String imageUrl, String licensePlate, String description) {
        super();
        this.brand = brand;
        this.model = model;
        this.category = category;
        this.dailyPrice = dailyPrice;
        this.available = available;
        this.fuelType = fuelType;
        this.transmission = transmission;
        this.seatingCapacity = seatingCapacity;
        this.imageUrl = imageUrl;
        this.licensePlate = licensePlate;
        this.description = description;
    }

    // Helper business method
    public String getFullName() {
        return brand + " " + model;
    }

    // Getters and Setters
    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public CarCategory getCategory() {
        return category;
    }

    public void setCategory(CarCategory category) {
        this.category = category;
    }

    public Double getDailyPrice() {
        return dailyPrice;
    }

    public void setDailyPrice(Double dailyPrice) {
        this.dailyPrice = dailyPrice;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public String getTransmission() {
        return transmission;
    }

    public void setTransmission(String transmission) {
        this.transmission = transmission;
    }

    public Integer getSeatingCapacity() {
        return seatingCapacity;
    }

    public void setSeatingCapacity(Integer seatingCapacity) {
        this.seatingCapacity = seatingCapacity;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Car{" +
                "id=" + getId() +
                ", brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", category=" + category +
                ", dailyPrice=" + dailyPrice +
                ", available=" + available +
                '}';
    }
}
