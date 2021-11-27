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

import com.sandpolis.core.instance.Entrypoint;
import com.sandpolis.core.instance.RuntimeVariable;
import com.sandpolis.core.instance.SystemProperty;
import com.sandpolis.core.instance.state.oid.Oid;

public class AgentContext {

	/**
	 * The connection cooldown.
	 */
	public static final RuntimeVariable<Integer> SERVER_COOLDOWN = RuntimeVariable.of(cfg -> {
		cfg.type = Integer.class;
		cfg.primary = Oid.of("com.sandpolis.core.agent:/profile/" + Entrypoint.data().uuid() + "/cooldown");
		cfg.secondary = SystemProperty.of("s7s.agent.cooldown");
	});

	/**
	 * The connection timeout.
	 */
	public static final RuntimeVariable<Integer> SERVER_TIMEOUT = RuntimeVariable.of(cfg -> {
		cfg.type = Integer.class;
		cfg.secondary = SystemProperty.of("s7s.agent.timeout");
		cfg.defaultValue = () -> AgentConfig.EMBEDDED.network().timeout();
	});

	/**
	 * The server addresses.
	 */
	public static final RuntimeVariable<String[]> SERVER_ADDRESS = RuntimeVariable.of(cfg -> {
		cfg.type = String[].class;
		cfg.secondary = SystemProperty.of("s7s.agent.address");
		cfg.defaultValue = () -> AgentConfig.EMBEDDED.network().servers().toArray(String[]::new);
	});

	/**
	 * The authentication certificate.
	 */
	public static final RuntimeVariable<String> AUTH_CERTIFICATE = RuntimeVariable.of(cfg -> {
		cfg.type = String.class;
		cfg.secondary = SystemProperty.of("s7s.agent.auth.type");
	});

	/**
	 * The authentication password.
	 */
	public static final RuntimeVariable<String> AUTH_PASSWORD = RuntimeVariable.of(cfg -> {
		cfg.type = String.class;
		cfg.secondary = SystemProperty.of("s7s.agent.auth.password");
	});
}
