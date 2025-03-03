package com.banquito.gateway.transacciones.banquito;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

//PRUEBA DEVOPS
@SpringBootApplication
@EnableFeignClients
public class BanquitoApplication {

	public static void main(String[] args) {
		System.setProperty("java.util.logging.manager", "org.jboss.logmanager.LogManager");
		SpringApplication.run(BanquitoApplication.class, args);
	}

}
