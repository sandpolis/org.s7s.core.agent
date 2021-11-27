//============================================================================//
//                                                                            //
//                         Copyright © 2015 Sandpolis                         //
//                                                                            //
//  This source file is subject to the terms of the Mozilla Public License    //
//  version 2. You may not use this file except in compliance with the MPL    //
//  as published by the Mozilla Foundation.                                   //
//                                                                            //
//============================================================================//
package com.sandpolis.core.agent;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sandpolis.core.agent.AgentConfig.AuthConfig;
import com.sandpolis.core.agent.AgentConfig.NetworkCfg;
import com.sandpolis.core.instance.Entrypoint;

public record AgentConfig(NetworkCfg network, AuthConfig auth) {

	private static final Logger log = LoggerFactory.getLogger(AgentConfig.class);

	public static final AgentConfig EMBEDDED = load();

	private static AgentConfig load() {

		try (var in = Entrypoint.data().main().getResourceAsStream("/config/com.sandpolis.core.agent.json")) {
			if (in != null) {
				return new ObjectMapper().readValue(in, AgentConfig.class);
			} else {
				log.debug("Config not found: /config/com.sandpolis.core.agent.json");
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return null;
	}

	public record NetworkCfg(List<String> servers, int timeout, boolean strict_certs, int polling_interval) {
	}

	public record AuthConfig(String password, String certificate) {
	}
}
