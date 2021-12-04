//============================================================================//
//                                                                            //
//                         Copyright © 2015 Sandpolis                         //
//                                                                            //
//  This source file is subject to the terms of the Mozilla Public License    //
//  version 2. You may not use this file except in compliance with the MPL    //
//  as published by the Mozilla Foundation.                                   //
//                                                                            //
//============================================================================//
package com.sandpolis.core.agent.init;

import static com.sandpolis.core.instance.connection.ConnectionStore.ConnectionStore;

import com.sandpolis.core.agent.AgentConfig;
import com.sandpolis.core.agent.AgentContext;
import com.sandpolis.core.instance.InitTask;
import com.sandpolis.core.instance.channel.client.ClientChannelInitializer;

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
