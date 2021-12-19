//============================================================================//
//                                                                            //
//            Copyright © 2015 - 2022 Sandpolis Software Foundation           //
//                                                                            //
//  This source file is subject to the terms of the Mozilla Public License    //
//  version 2. You may not use this file except in compliance with the MPLv2. //
//                                                                            //
//============================================================================//
package org.s7s.core.agent;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.s7s.core.agent.AgentConfig.AuthConfig;
import org.s7s.core.agent.AgentConfig.NetworkCfg;

public record AgentConfig(NetworkCfg network, AuthConfig auth) {

	private static final Logger log = LoggerFactory.getLogger(AgentConfig.class);

	public static final AgentConfig EMBEDDED = load();

	private static AgentConfig load() {

		try (var in = AgentConfig.class.getResourceAsStream("/org.s7s.core.agent.json")) {
			if (in != null) {
				return new ObjectMapper().readValue(in, AgentConfig.class);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return null;
	}

	public record NetworkCfg(String server_address, int timeout, boolean strict_certs, int polling_interval) {
	}

	public record AuthConfig(String password, String certificate) {
	}
}
