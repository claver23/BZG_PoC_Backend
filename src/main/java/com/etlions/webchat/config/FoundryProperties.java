package com.etlions.webchat.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "foundry")
public record FoundryProperties(
		String endpoint,
		String agentName,
		String agentVersion,
		String apiKey
) {}
