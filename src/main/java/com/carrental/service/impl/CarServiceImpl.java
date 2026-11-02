package com.carrental.service.impl;

import com.carrental.dto.CarDto;
import com.carrental.entity.Car;
import com.carrental.enums.CarCategory;
import com.carrental.exception.ResourceNotFoundException;
import com.carrental.repository.CarRepository;
import com.carrental.service.CarService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Service implementation for car catalog management.
 * Demonstrates Collections (List, ArrayList), CRUD operations via Spring Data JPA.
 */
@Service
@Transactional
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;

    public CarServiceImpl(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Car> findAll() {
        return new ArrayList<>(carRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Car> findAvailableCars() {
        return new ArrayList<>(carRepository.findByAvailableTrue());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Car> findByCategory(CarCategory category) {
        return new ArrayList<>(carRepository.findByCategoryAndAvailableTrue(category));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Car> searchAndFilterCars(CarCategory category, Boolean available, String search) {
        String trimmedSearch = (search != null && !search.trim().isEmpty()) ? search.trim() : null;
        List<Car> cars = carRepository.filterCars(category, available, trimmedSearch);
        return new ArrayList<>(cars);
    }

    @Override
    @Transactional(readOnly = true)
    public Car findById(Long id) {
        return carRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found with ID: " + id));
    }

    @Override
    public Car createCar(CarDto carDto) {
        if (carDto.getLicensePlate() != null && carRepository.existsByLicensePlate(carDto.getLicensePlate().trim().toUpperCase())) {
            throw new IllegalArgumentException("A car with license plate " + carDto.getLicensePlate() + " already exists.");
        }

        Car car = new Car();
        copyDtoToEntity(carDto, car);
        return carRepository.save(car);
    }

    @Override
    public Car updateCar(Long id, CarDto carDto) {
        Car existing = findById(id);
        copyDtoToEntity(carDto, existing);
        return carRepository.save(existing);
    }

    @Override
    public void deleteCar(Long id) {
        Car car = findById(id);
        carRepository.delete(car);
    }

    @Override
    public Car setAvailability(Long id, boolean available) {
        Car car = findById(id);
        car.setAvailable(available);
        return carRepository.save(car);
    }

    @Override
    @Transactional(readOnly = true)
    public long countTotalCars() {
        return carRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long countAvailableCars() {
        return carRepository.countByAvailableTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public long countRentedCars() {
        return carRepository.countByAvailableFalse();
    }

    private void copyDtoToEntity(CarDto dto, Car car) {
        car.setBrand(dto.getBrand().trim());
        car.setModel(dto.getModel().trim());
        car.setCategory(dto.getCategory());
        car.setDailyPrice(dto.getDailyPrice());
        car.setAvailable(dto.isAvailable());
        if (dto.getFuelType() != null) car.setFuelType(dto.getFuelType().trim());
        if (dto.getTransmission() != null) car.setTransmission(dto.getTransmission().trim());
        if (dto.getSeatingCapacity() != null) car.setSeatingCapacity(dto.getSeatingCapacity());
        if (dto.getImageUrl() != null) car.setImageUrl(dto.getImageUrl().trim());
        if (dto.getLicensePlate() != null) car.setLicensePlate(dto.getLicensePlate().trim().toUpperCase());
        if (dto.getDescription() != null) car.setDescription(dto.getDescription().trim());
    }
}
