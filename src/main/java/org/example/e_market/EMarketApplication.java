package org.example.e_market;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class EMarketApplication {

    public static void main(String[] args) {
        SpringApplication.run(EMarketApplication.class, args);
    }

}
