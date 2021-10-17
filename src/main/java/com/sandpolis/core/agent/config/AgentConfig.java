package com.sandpolis.core.agent.config;

import java.io.IOException;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sandpolis.core.instance.Entrypoint;
import com.sandpolis.core.instance.config.BuildConfig;

public record AgentConfig() {

	private static Optional<AgentConfig> instance;

	public static Optional<AgentConfig> get() {
		if (instance == null) {
			try (var in = Entrypoint.data().main().getResourceAsStream("/config/agent.json")) {
				if (in != null) {
					instance = Optional.of(new ObjectMapper().readValue(in, AgentConfig.class));
				} else {
					instance = Optional.empty();
				}
			} catch (IOException e) {
				instance = Optional.empty();
			}
		}
		return instance;
	}
}
