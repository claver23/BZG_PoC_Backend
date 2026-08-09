package com.etlions.webchat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import com.etlions.webchat.config.FoundryProperties;

@SpringBootApplication
@EnableConfigurationProperties(FoundryProperties.class)
public class WebchatApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebchatApplication.class, args);
	}

}
