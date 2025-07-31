package com.sammy.book_network;

import com.sammy.book_network.role.Role;
import com.sammy.book_network.role.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

import java.security.SecureRandom;
import java.util.Base64;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware") // reference the auditing bean
@EnableAsync
public class BookNetworkApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookNetworkApplication.class, args);
    }

    @Bean
    public CommandLineRunner runner(RoleRepository roleRepository) {
        return args -> {
            // make sure that when tha application is started, a default role of "USER"
            // is created.
            if (roleRepository.findByName("USER").isEmpty()) {
                roleRepository.save(Role.builder().name("USER").build());
            }
            ;
        };
    }


}

