package com.example.techstore;

import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@EnableCaching
@EnableAsync
public class TechStoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(TechStoreApplication.class, args);
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public Hibernate6Module hibernate6Module() {
        return new Hibernate6Module();
    }
}
