package com.etlions.webchat.dto;

public record ChatRequest(String message, String previousResponseId) {}
