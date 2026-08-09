package com.etlions.webchat.function;

import java.util.Optional;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PingFunction {

	@FunctionName("ping")
	public static HttpResponseMessage ping(
		@HttpTrigger(
			name = "req",
			methods = { HttpMethod.GET },
			authLevel = AuthorizationLevel.FUNCTION,
			route = "ping"
		) HttpRequestMessage<Optional<String>> request,
		ExecutionContext context) {

		context.getLogger().info("Ping request received");
		return request.createResponseBuilder(HttpStatus.OK).body("pong").build();
	}
}

