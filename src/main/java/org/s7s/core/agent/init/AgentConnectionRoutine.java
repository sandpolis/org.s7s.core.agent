//============================================================================//
//                                                                            //
//            Copyright © 2015 - 2022 Sandpolis Software Foundation           //
//                                                                            //
//  This source file is subject to the terms of the Mozilla Public License    //
//  version 2. You may not use this file except in compliance with the MPLv2. //
//                                                                            //
//============================================================================//
package org.s7s.core.agent.init;

import static org.s7s.core.instance.connection.ConnectionStore.ConnectionStore;

import org.s7s.core.agent.AgentConfig;
import org.s7s.core.agent.AgentContext;
import org.s7s.core.instance.InitTask;
import org.s7s.core.instance.channel.client.ClientChannelInitializer;

public class AgentConnectionRoutine extends InitTask {

	@Override
	public TaskOutcome run(TaskOutcome.Factory outcome) throws Exception {
		ConnectionStore.connect(config -> {
			config.address(AgentContext.SERVER_ADDRESS.get());
			config.timeout = AgentContext.SERVER_TIMEOUT.get();
			config.bootstrap.handler(new ClientChannelInitializer(struct -> {
				struct.clientTlsInsecure();
			}));
		});

		return outcome.succeeded();
	}

	@Override
	public String description() {
		return "Begin connection routine";
	}

}
