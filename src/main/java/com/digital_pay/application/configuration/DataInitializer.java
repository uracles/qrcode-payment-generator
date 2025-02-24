package com.digital_pay.application.configuration;

import com.digital_pay.application.entity.Merchant;
import com.digital_pay.application.entity.User;
import com.digital_pay.application.repository.MerchantRepository;
import com.digital_pay.application.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initDatabase(
            @Autowired UserRepository userRepository,
            @Autowired MerchantRepository merchantRepository) {

        return args -> {
            System.out.println("Initializing database with test data...");

            // Check if user with ID 1 exists, if not create it
            if (!userRepository.existsById(1L)) {
                System.out.println("Creating test user...");
                User user = new User();
                user.setId(1L);
                user.setBalance(new BigDecimal("1000.00"));
                userRepository.save(user);
                System.out.println("Test user created.");
            } else {
                System.out.println("Test user already exists.");
            }

            // Check if merchant with ID 12345 exists, if not create it
            if (!merchantRepository.existsById(12345L)) {
                System.out.println("Creating test merchant...");
                Merchant merchant = new Merchant();
                merchant.setId(12345L);
                merchant.setBalance(new BigDecimal("1000.00"));
                merchantRepository.save(merchant);
                System.out.println("Test merchant created.");
            } else {
                System.out.println("Test merchant already exists.");
            }

            // Print all users and merchants for verification
            System.out.println("All users:");
            userRepository.findAll().forEach(u ->
                    System.out.println("User ID: " + u.getId() + ", Balance: " + u.getBalance()));

            System.out.println("All merchants:");
            merchantRepository.findAll().forEach(m ->
                    System.out.println("Merchant ID: " + m.getId() + ", Balance: " + m.getBalance()));

            System.out.println("Database initialization complete.");
        };
    }
}