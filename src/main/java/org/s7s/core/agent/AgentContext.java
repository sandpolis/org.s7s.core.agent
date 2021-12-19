//============================================================================//
//                                                                            //
//            Copyright © 2015 - 2022 Sandpolis Software Foundation           //
//                                                                            //
//  This source file is subject to the terms of the Mozilla Public License    //
//  version 2. You may not use this file except in compliance with the MPLv2. //
//                                                                            //
//============================================================================//
package org.s7s.core.agent;

import org.s7s.core.foundation.S7SEnvironmentVariable;
import org.s7s.core.foundation.S7SSystemProperty;
import org.s7s.core.instance.Entrypoint;
import org.s7s.core.instance.RuntimeVariable;
import org.s7s.core.instance.state.oid.Oid;

public class AgentContext {

	/**
	 * The connection cooldown.
	 */
	public static final RuntimeVariable<Integer> SERVER_COOLDOWN = RuntimeVariable.of(cfg -> {
		cfg.type = Integer.class;
		cfg.primary = Oid.of("org.s7s.core.agent:/profile/" + Entrypoint.data().uuid() + "/cooldown");
		cfg.secondary = S7SSystemProperty.of("s7s.agent.cooldown");
	});

	/**
	 * The connection timeout.
	 */
	public static final RuntimeVariable<Integer> SERVER_TIMEOUT = RuntimeVariable.of(cfg -> {
		cfg.type = Integer.class;
		cfg.secondary = S7SSystemProperty.of("s7s.agent.timeout");
		cfg.defaultValue = () -> AgentConfig.EMBEDDED.network().timeout();
	});

	/**
	 * The server address.
	 */
	public static final RuntimeVariable<String> SERVER_ADDRESS = RuntimeVariable.of(cfg -> {
		cfg.type = String.class;
		cfg.secondary = S7SSystemProperty.of("s7s.agent.address");
		cfg.tertiary = S7SEnvironmentVariable.of("S7S_SERVER_ADDRESS");
		cfg.defaultValue = () -> AgentConfig.EMBEDDED.network().server_address();
	});

	/**
	 * The authentication certificate.
	 */
	public static final RuntimeVariable<String> AUTH_CERTIFICATE = RuntimeVariable.of(cfg -> {
		cfg.type = String.class;
		cfg.secondary = S7SSystemProperty.of("s7s.agent.auth.type");
	});

	/**
	 * The authentication password.
	 */
	public static final RuntimeVariable<String> AUTH_PASSWORD = RuntimeVariable.of(cfg -> {
		cfg.type = String.class;
		cfg.secondary = S7SSystemProperty.of("s7s.agent.auth.password");
	});
}
