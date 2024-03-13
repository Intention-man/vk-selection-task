package com.intentionman.vkselectiontask;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class VkSelectionTaskApplication {
	//TODO починить swagger, чтобы красиво все открывалось по ссылке http://localhost:8181/api/swagger-ui/index.html

	public static void main(String[] args) {
		SpringApplication.run(VkSelectionTaskApplication.class, args);
	}


}
