package com.example.volleybot;

import com.example.volleybot.entity.Player;
import com.example.volleybot.repository.PlayerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class VolleybotApplication {


    public static void main(String[] args) {
        SpringApplication.run(VolleybotApplication.class, args);
    }

  /*  @Bean
    public CommandLineRunner run(PlayerRepository repository) {
        return args -> {
//            addNewPlayer(repository);
        };
    }

    private void addNewPlayer(PlayerRepository repository) {
        repository.save(new Player(123L, "Виктор Кондратьев"));
    }
*/
}
