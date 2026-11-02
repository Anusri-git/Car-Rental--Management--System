package com.carrental.controller;

import com.carrental.dto.ApiResponse;
import com.carrental.entity.Car;
import com.carrental.enums.CarCategory;
import com.carrental.service.CarService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cars")
public class CarController {

    private final CarService carService;

    public CarController(CarService carService) {
        this.carService = carService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Car>>> getAllCars(
            @RequestParam(required = false) CarCategory category,
            @RequestParam(required = false) Boolean available,
            @RequestParam(required = false) String search) {
        List<Car> cars = carService.searchAndFilterCars(category, available, search);
        return ResponseEntity.ok(ApiResponse.success("Cars retrieved successfully", cars));
    }

    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<Car>>> getAvailableCars() {
        List<Car> cars = carService.findAvailableCars();
        return ResponseEntity.ok(ApiResponse.success("Available cars retrieved successfully", cars));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Car>> getCarById(@PathVariable Long id) {
        Car car = carService.findById(id);
        return ResponseEntity.ok(ApiResponse.success("Car found", car));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<List<Car>>> getCarsByCategory(@PathVariable CarCategory category) {
        List<Car> cars = carService.findByCategory(category);
        return ResponseEntity.ok(ApiResponse.success("Cars in category " + category, cars));
    }
}
