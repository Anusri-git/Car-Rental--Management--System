package com.carrental.repository;

import com.carrental.entity.Car;
import com.carrental.enums.CarCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

    List<Car> findByAvailableTrue();

    List<Car> findByCategoryAndAvailableTrue(CarCategory category);

    List<Car> findByCategory(CarCategory category);

    long countByAvailableTrue();

    long countByAvailableFalse();

    boolean existsByLicensePlate(String licensePlate);

    @Query("SELECT c FROM Car c WHERE " +
           "(:category IS NULL OR c.category = :category) AND " +
           "(:available IS NULL OR c.available = :available) AND " +
           "(:search IS NULL OR LOWER(c.brand) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(c.model) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<Car> filterCars(@Param("category") CarCategory category,
                         @Param("available") Boolean available,
                         @Param("search") String search);
}
