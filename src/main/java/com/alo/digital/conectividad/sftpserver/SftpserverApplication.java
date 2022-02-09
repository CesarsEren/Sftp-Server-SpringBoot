package com.alo.digital.conectividad.sftpserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SftpserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(SftpserverApplication.class, args);
		while(true);
	}

}
