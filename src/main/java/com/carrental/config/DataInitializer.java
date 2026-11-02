package com.carrental.config;

import com.carrental.entity.Admin;
import com.carrental.entity.Car;
import com.carrental.entity.User;
import com.carrental.enums.CarCategory;
import com.carrental.enums.Role;
import com.carrental.repository.CarRepository;
import com.carrental.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Initializes default Admin, sample customer, and attractive car inventory on startup.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CarRepository carRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, 
                           CarRepository carRepository, 
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.carRepository = carRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedUsers();
        seedCars();
    }

    private void seedUsers() {
        // Seed default Admin if not exists
        if (!userRepository.existsByEmail("admin@carrental.com")) {
            Admin admin = new Admin(
                    "System Admin",
                    "admin@carrental.com",
                    "+1-800-555-0199",
                    passwordEncoder.encode("Admin@123"),
                    "Fleet Operations",
                    "ADM-7701"
            );
            userRepository.save(admin);
            System.out.println(">>> Sample Admin created: admin@carrental.com / Admin@123");
        }

        // Seed demo customer if not exists
        if (!userRepository.existsByEmail("john@example.com")) {
            User customer = new User(
                    "John Anderson",
                    "john@example.com",
                    "+1-555-014-8832",
                    passwordEncoder.encode("User@123"),
                    Role.ROLE_USER
            );
            userRepository.save(customer);
            System.out.println(">>> Sample User created: john@example.com / User@123");
        }
    }

    private void seedCars() {
        if (carRepository.count() > 0) {
            return;
        }

        List<Car> initialCars = Arrays.asList(
                // Hatchbacks
                new Car("Volkswagen", "Polo GT TSI", CarCategory.HATCHBACK, 45.0, true, 
                        "Petrol", "Automatic", 5, 
                        "https://images.unsplash.com/photo-1541899481282-d53bffe3c35d?auto=format&fit=crop&w=800&q=80", 
                        "DL-01-VW-1001", "Sporty hatchback offering dynamic turbo performance and agile city handling."),
                
                new Car("Hyundai", "i20 N-Line", CarCategory.HATCHBACK, 40.0, true, 
                        "Petrol", "Manual", 5, 
                        "https://images.unsplash.com/photo-1580273916550-e323be2ae537?auto=format&fit=crop&w=800&q=80", 
                        "MH-02-HY-2002", "Feature-rich sporty hatchback with tuned suspension and premium interior tech."),

                new Car("MINI", "Cooper S", CarCategory.HATCHBACK, 75.0, true, 
                        "Petrol", "Automatic", 4, 
                        "https://images.unsplash.com/photo-1617814076367-b759c7d7e738?auto=format&fit=crop&w=800&q=80", 
                        "KA-03-MC-3003", "Iconic go-kart handling with distinctive British styling and turbocharged power."),

                // Sedans
                new Car("Honda", "Civic Touring", CarCategory.SEDAN, 55.0, true, 
                        "Petrol", "Automatic", 5, 
                        "https://images.unsplash.com/photo-1606016159991-dfe4f2746ad5?auto=format&fit=crop&w=800&q=80", 
                        "KA-04-HC-4004", "Smooth, fuel-efficient sedan equipped with Honda Sensing driver assistance."),

                new Car("Toyota", "Camry Hybrid", CarCategory.SEDAN, 75.0, true, 
                        "Hybrid", "Automatic", 5, 
                        "https://images.unsplash.com/photo-1621007947382-bb3c3994e3fb?auto=format&fit=crop&w=800&q=80", 
                        "DL-05-TC-5005", "Whisper-quiet luxury hybrid sedan with executive legroom and supreme ride comfort."),

                new Car("BMW", "3 Series 330i M Sport", CarCategory.SEDAN, 115.0, true, 
                        "Petrol", "Automatic", 5, 
                        "https://images.unsplash.com/photo-1555215695-3004980ad54e?auto=format&fit=crop&w=800&q=80", 
                        "MH-06-BM-6006", "The benchmark sports sedan featuring rear-wheel drive dynamics and premium leather."),

                // SUVs
                new Car("Hyundai", "Creta SX(O)", CarCategory.SUV, 60.0, true, 
                        "Diesel", "Automatic", 5, 
                        "https://images.unsplash.com/photo-1519641471654-76ce0107ad1b?auto=format&fit=crop&w=800&q=80", 
                        "DL-07-HC-7007", "Spacious compact SUV with panoramic sunroof, ventilated seats and commanding view."),

                new Car("Toyota", "Fortuner Legender 4x4", CarCategory.SUV, 125.0, true, 
                        "Diesel", "Automatic", 7, 
                        "https://images.unsplash.com/photo-1533473359331-0135ef1b58bf?auto=format&fit=crop&w=800&q=80", 
                        "KA-08-TF-8008", "Rugged 7-seater off-road SUV ready for mountain expeditions and highway cruising."),

                new Car("Range Rover", "Velar R-Dynamic", CarCategory.SUV, 175.0, true, 
                        "Petrol", "Automatic", 5, 
                        "https://images.unsplash.com/photo-1563720223185-11003d516935?auto=format&fit=crop&w=800&q=80", 
                        "MH-09-RR-9009", "Avant-garde British luxury SUV with flush door handles and dual-touch infotainment."),

                // Luxury
                new Car("Mercedes-Benz", "C-Class C300", CarCategory.LUXURY, 160.0, true, 
                        "Petrol", "Automatic", 5, 
                        "https://images.unsplash.com/photo-1618843479313-40f8afb4b4d8?auto=format&fit=crop&w=800&q=80", 
                        "DL-10-MB-1010", "Sophisticated German engineering with Burmester surround sound and ambient lighting."),

                new Car("BMW", "M4 Competition Coupe", CarCategory.LUXURY, 240.0, true, 
                        "Petrol", "Automatic", 4, 
                        "https://images.unsplash.com/photo-1580274455191-1c62238fa333?auto=format&fit=crop&w=800&q=80", 
                        "KA-11-BM-1111", "503-horsepower twin-turbo coupe built for pure adrenaline and precision control."),

                new Car("Porsche", "911 Carrera GTS", CarCategory.LUXURY, 295.0, true, 
                        "Petrol", "Automatic", 2, 
                        "https://images.unsplash.com/photo-1503376780353-7e6692767b70?auto=format&fit=crop&w=800&q=80", 
                        "MH-12-P9-1212", "Timeless sports car perfection with blistering acceleration and razor-sharp handling.")
        );

        carRepository.saveAll(initialCars);
        System.out.println(">>> Sample Cars seeded: " + initialCars.size() + " vehicles added.");
    }
}
