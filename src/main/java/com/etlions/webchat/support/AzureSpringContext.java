package com.etlions.webchat.support;

import com.etlions.webchat.WebchatApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public final class AzureSpringContext {

	private static volatile ConfigurableApplicationContext context;

	private AzureSpringContext() {
	}

	public static ConfigurableApplicationContext getContext() {
		ConfigurableApplicationContext current = context;
		if (current == null) {
			synchronized (AzureSpringContext.class) {
				current = context;
				if (current == null) {
					current = new SpringApplicationBuilder(WebchatApplication.class)
							.web(WebApplicationType.NONE)
							.run();
					context = current;
				}
			}
		}
		return current;
	}
}
