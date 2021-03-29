//============================================================================//
//                                                                            //
//                         Copyright © 2015 Sandpolis                         //
//                                                                            //
//  This source file is subject to the terms of the Mozilla Public License    //
//  version 2. You may not use this file except in compliance with the MPL    //
//  as published by the Mozilla Foundation.                                   //
//                                                                            //
//============================================================================//
package com.sandpolis.core.agent.config;

import com.sandpolis.core.foundation.config.ConfigProperty;
import com.sandpolis.core.instance.config.DefaultConfigProperty;

public final class CfgAgent {

	/**
	 * The connection cooldown.
	 */
	public static final ConfigProperty<Integer> SERVER_COOLDOWN = new DefaultConfigProperty<>(Integer.class,
			"s7s.agent.cooldown");

	/**
	 * The connection timeout.
	 */
	public static final ConfigProperty<Integer> SERVER_TIMEOUT = new DefaultConfigProperty<>(Integer.class,
			"s7s.agent.timeout");

	/**
	 * The primary server.
	 */
	public static final ConfigProperty<String> SERVER_ADDRESS = new DefaultConfigProperty<>(String.class,
			"s7s.agent.address");

	/**
	 * The authentication type.
	 */
	public static final ConfigProperty<String> AUTH_TYPE = new DefaultConfigProperty<>(String.class,
			"s7s.agent.auth.type");

	/**
	 * The authentication password.
	 */
	public static final ConfigProperty<String> AUTH_PASSWORD = new DefaultConfigProperty<>(String.class,
			"s7s.agent.auth.password");

	private CfgAgent() {
	}
}
