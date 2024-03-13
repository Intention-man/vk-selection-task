package com.intentionman.vkselectiontask;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class VkSelectionTaskApplication {
	//TODO реализовать websocket
	public static void main(String[] args) {
		SpringApplication.run(VkSelectionTaskApplication.class, args);
	}


}
