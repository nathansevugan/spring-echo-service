package com.echo.springbootechoservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.echo")
public class SpringBootEchoServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootEchoServiceApplication.class, args);
	}
}
