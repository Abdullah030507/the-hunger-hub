package com.example.bhajiDishStore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;


@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class BhajiDishStoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(BhajiDishStoreApplication.class, args);
	}

}
