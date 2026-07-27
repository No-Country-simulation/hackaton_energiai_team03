package dev.team3.chillywatts;

import dev.team3.chillywatts.principal.Principal;
import dev.team3.chillywatts.repository.FreezerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ChillywattsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChillywattsApplication.class, args);
    }
}