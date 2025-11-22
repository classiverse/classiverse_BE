package com.classiverse.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing; // 추가

@EnableJpaAuditing
@SpringBootApplication
public class ClassiverseBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClassiverseBackendApplication.class, args);
    }

}