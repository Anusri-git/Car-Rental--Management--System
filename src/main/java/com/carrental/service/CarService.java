package com.carrental.service;

import com.carrental.dto.CarDto;
import com.carrental.entity.Car;
import com.carrental.enums.CarCategory;

import java.util.List;

public interface CarService {
    List<Car> findAll();
    List<Car> findAvailableCars();
    List<Car> findByCategory(CarCategory category);
    List<Car> searchAndFilterCars(CarCategory category, Boolean available, String search);
    Car findById(Long id);
    Car createCar(CarDto carDto);
    Car updateCar(Long id, CarDto carDto);
    void deleteCar(Long id);
    Car setAvailability(Long id, boolean available);
    long countTotalCars();
    long countAvailableCars();
    long countRentedCars();
}
