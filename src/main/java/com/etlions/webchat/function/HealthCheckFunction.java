package com.etlions.webchat.function;

import java.util.Optional;
import java.util.function.Function;

import com.etlions.webchat.dto.HealthCheckResponse;
import com.etlions.webchat.service.HealthCheckService;
import com.etlions.webchat.support.AzureSpringContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HealthCheckFunction {

	private static final ObjectMapper MAPPER = new ObjectMapper();

	@Bean
	public Function<String, HealthCheckResponse> healthcheck(HealthCheckService healthCheckService) {
		return ignored -> healthCheckService.getHealth();
	}

	@FunctionName("healthcheck")
	public HttpResponseMessage execute(
		@HttpTrigger(
			name = "request",
			methods = { HttpMethod.GET },
			authLevel = AuthorizationLevel.FUNCTION,
			route = "healthcheck"
		) HttpRequestMessage<Optional<String>> request,
		ExecutionContext context) {
		try {
			HealthCheckResponse response = getAzureHealthCheckService().getHealth();
			return request.createResponseBuilder(HttpStatus.OK)
				.header("Content-Type", "application/json")
				.body(MAPPER.writeValueAsString(response))
				.build();
		} catch (Exception e) {
			context.getLogger().severe("Healthcheck error: " + e);
			return request.createResponseBuilder(HttpStatus.OK)
				.header("Content-Type", "application/json")
				.body("{\"error\":\"" + e.getClass().getName() + ": " + e.getMessage() + "\"}")
				.build();
		}
	}

	private HealthCheckService getAzureHealthCheckService() {
		return AzureSpringContext.getContext().getBean(HealthCheckService.class);
	}
}
