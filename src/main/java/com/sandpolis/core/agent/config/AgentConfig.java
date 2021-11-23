package com.sandpolis.core.agent.config;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sandpolis.core.foundation.S7SIPAddress;
import com.sandpolis.core.instance.Entrypoint;
import com.sandpolis.core.instance.config.ConfigProperty;

public record AgentConfig(NetworkCfg network, AuthConfig auth) {

	private static final Logger log = LoggerFactory.getLogger(AgentConfig.class);

	public static final AgentConfig INSTANCE = load();

	public static final AgentConfig DEFAULT = new AgentConfig(new NetworkCfg(null, 0, false, 0), new AuthConfig());

	private static AgentConfig load() {

		try (var in = Entrypoint.data().main().getResourceAsStream("/config/com.sandpolis.core.agent.json")) {
			if (in != null) {
				return new ObjectMapper().readValue(in, AgentConfig.class);
			} else {
				log.debug("Instance config not found: /config/com.sandpolis.core.agent.json");
			}
		} catch (IOException e) {
			log.error("Failed to read instance config: /config/com.sandpolis.core.agent.json", e);
		}

		return DEFAULT;
	}

	public record NetworkCfg(List<String> servers, int timeout, boolean strict_certs, int polling_interval) {
	}

	public record AuthConfig() {
	}

	/**
	 * The connection cooldown.
	 */
	public static final ConfigProperty<Integer> SERVER_COOLDOWN = ConfigProperty.evaluate(Integer.class,
			"s7s.agent.cooldown");

	/**
	 * The connection timeout.
	 */
	public static final ConfigProperty<Integer> SERVER_TIMEOUT = ConfigProperty.evaluate(Integer.class,
			"s7s.agent.timeout", INSTANCE.network().timeout());

	/**
	 * The primary server.
	 */
	public static final ConfigProperty<String> SERVER_ADDRESS = ConfigProperty.evaluate(String.class,
			"s7s.agent.address", S7SIPAddress::isValidIPv4);

	/**
	 * The authentication type.
	 */
	public static final ConfigProperty<String> AUTH_TYPE = ConfigProperty.evaluate(String.class, "s7s.agent.auth.type");

	/**
	 * The authentication password.
	 */
	public static final ConfigProperty<String> AUTH_PASSWORD = ConfigProperty.evaluate(String.class,
			"s7s.agent.auth.password");
}
