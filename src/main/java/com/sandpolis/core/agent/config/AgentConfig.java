package com.sandpolis.core.agent.config;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sandpolis.core.instance.Entrypoint;

public record AgentConfig(boolean development, NetworkCfg network) {

	private static final Logger log = LoggerFactory.getLogger(AgentConfig.class);

	private static Optional<AgentConfig> instance;

	public static Optional<AgentConfig> get() {
		if (instance == null) {
			try (var in = Entrypoint.data().main().getResourceAsStream("/config/agent.json")) {
				if (in != null) {
					instance = Optional.of(new ObjectMapper().readValue(in, AgentConfig.class));
				} else {
					log.debug("Instance config not found: /config/agent.json");
					instance = Optional.empty();
				}
			} catch (IOException e) {
				log.error("Failed to read instance config: /config/agent.json", e);
				instance = Optional.empty();
			}
		}
		return instance;
	}

	public record NetworkCfg(List<String> address) {
	}
}
