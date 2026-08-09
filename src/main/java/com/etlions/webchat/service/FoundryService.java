package com.etlions.webchat.service;

import com.etlions.webchat.dto.ChatRequest;
import com.etlions.webchat.dto.ChatResponse;

public interface FoundryService {
	ChatResponse chat(ChatRequest request);
}
