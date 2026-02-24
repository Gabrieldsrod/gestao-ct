package com.gabrieldsrod.gestao_ct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GestaoCtApplication {

	public static void main(String[] args) {
		SpringApplication.run(GestaoCtApplication.class, args);
	}

}
