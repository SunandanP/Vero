package com.blackbox.vero;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class VeroApplication {

	public static void main(String[] args) {
		SpringApplication.run(VeroApplication.class, args);
	}

}
