package com.carrental;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CarRentalApplication {

    public static void main(String[] args) {
        SpringApplication.run(CarRentalApplication.class, args);
        System.out.println("=================================================");
        System.out.println("  CAR RENTAL MANAGEMENT SYSTEM STARTED ON PORT 8080");
        System.out.println("  URL: http://localhost:8080");
        System.out.println("=================================================");
    }
}
