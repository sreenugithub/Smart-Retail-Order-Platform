package com.retail.notification_service;

import com.retail.notification_service.config.KafkaSslStoreLocationEnvironmentPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NotificationServiceApplication {

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(NotificationServiceApplication.class);
		application.addListeners(new KafkaSslStoreLocationEnvironmentPostProcessor());
		application.run(args);
	}

}
