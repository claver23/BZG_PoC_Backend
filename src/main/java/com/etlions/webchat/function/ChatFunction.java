package com.etlions.webchat.function;

import java.util.Optional;
import java.util.function.Function;

import com.etlions.webchat.dto.ChatRequest;
import com.etlions.webchat.dto.ChatResponse;
import com.etlions.webchat.service.FoundryService;
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
public class ChatFunction {

	private static final ObjectMapper MAPPER = new ObjectMapper();

	@Bean
	public Function<ChatRequest, ChatResponse> chat(FoundryService foundryService) {
		return req -> foundryService.chat(req);
	}

	@FunctionName("chat")
	public HttpResponseMessage execute(
			@HttpTrigger(
					name = "request",
					methods = { HttpMethod.POST, HttpMethod.OPTIONS },
					authLevel = AuthorizationLevel.FUNCTION,
					route = "chat"
			) HttpRequestMessage<Optional<String>> request,
			ExecutionContext context) {

		// Handle CORS preflight
		if (request.getHttpMethod() == HttpMethod.OPTIONS) {
			return request.createResponseBuilder(HttpStatus.OK)
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Methods", "POST, OPTIONS")
					.header("Access-Control-Allow-Headers", "Content-Type")
					.build();
		}

		try {
			String body = request.getBody().orElse("{}");
			ChatRequest chatRequest = MAPPER.readValue(body, ChatRequest.class);

			FoundryService service = AzureSpringContext.getContext().getBean(FoundryService.class);
			ChatResponse response = service.chat(chatRequest);

			return request.createResponseBuilder(HttpStatus.OK)
					.header("Content-Type", "application/json")
					.header("Access-Control-Allow-Origin", "*")
					.body(MAPPER.writeValueAsString(response))
					.build();

		} catch (Exception e) {
			context.getLogger().severe("Chat error: " + e.getMessage());
			return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
					.header("Content-Type", "application/json")
					.header("Access-Control-Allow-Origin", "*")
					.body("{\"error\":\"" + e.getMessage().replace("\"", "'") + "\"}")
					.build();
		}
	}
}
