package com.hamtaro.sunflowerplate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SunflowerPlateProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(SunflowerPlateProjectApplication.class, args);
	}

}
