package com.etlions.webchat.service.impl;

import java.util.HashMap;
import java.util.Map;

import com.etlions.webchat.config.FoundryProperties;
import com.etlions.webchat.dto.ChatRequest;
import com.etlions.webchat.dto.ChatResponse;
import com.etlions.webchat.service.FoundryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class FoundryServiceImpl implements FoundryService {

	private static final Logger log = LoggerFactory.getLogger(FoundryServiceImpl.class);
	private static final String API_VERSION = "2025-05-15-preview";

	private final RestClient restClient;
	private final FoundryProperties props;

	public FoundryServiceImpl(FoundryProperties props) {
		this.props = props;
		this.restClient = RestClient.builder()
				.baseUrl(props.endpoint())
				.build();
	}

	@Override
	@SuppressWarnings("unchecked")
	public ChatResponse chat(ChatRequest request) {
		Map<String, Object> body = new HashMap<>();
		body.put("input", request.message());
		body.put("agent", Map.of(
				"name", props.agentName(),
				"version", props.agentVersion(),
				"type", "agent_reference"
		));
		if (request.previousResponseId() != null && !request.previousResponseId().isBlank()) {
			body.put("previous_response_id", request.previousResponseId());
		}

		Map<String, Object> response = restClient.post()
				.uri("/openai/responses?api-version=" + API_VERSION)
				.header("api-key", props.apiKey())
				.contentType(MediaType.APPLICATION_JSON)
				.body(body)
				.retrieve()
				.body(Map.class);

		String responseId = (String) response.get("id");

		String text = (String) response.get("output_text");
		if (text == null || text.isBlank()) {
			try {
				var outputList = (java.util.List<?>) response.get("output");
				if (outputList != null && !outputList.isEmpty()) {
					var firstOutput = (java.util.Map<?, ?>) outputList.get(0);
					var contentList = (java.util.List<?>) firstOutput.get("content");
					if (contentList != null && !contentList.isEmpty()) {
						var firstContent = (java.util.Map<?, ?>) contentList.get(0);
						text = (String) firstContent.get("text");
					}
				}
			} catch (Exception e) {
				log.warn("Could not extract nested text: {}", e.getMessage());
			}
		}
		return new ChatResponse(text, responseId);
	}
}
