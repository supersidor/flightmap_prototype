package com.supersidor.booratino;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class BooratinoApplication {

	public static void main(String[] args) {
		SpringApplication.run(BooratinoApplication.class, args);
	}
}
