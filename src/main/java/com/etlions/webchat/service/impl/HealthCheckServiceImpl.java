package com.etlions.webchat.service.impl;

import java.time.Instant;
import com.etlions.webchat.dto.HealthCheckResponse;
import com.etlions.webchat.service.HealthCheckService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class HealthCheckServiceImpl implements HealthCheckService {

	private final String applicationName;

	public HealthCheckServiceImpl(@Value("${spring.application.name}") String applicationName) {
		this.applicationName = applicationName;
	}

	@Override
	public HealthCheckResponse getHealth() {
		return new HealthCheckResponse("UP", applicationName, Instant.now().toString());
	}
}
