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

import static com.sandpolis.core.net.connection.ConnectionStore.ConnectionStore;

import com.sandpolis.core.agent.config.CfgAgent;
import com.sandpolis.core.instance.InitTask;
import com.sandpolis.core.instance.TaskOutcome;
import com.sandpolis.core.net.channel.client.ClientChannelInitializer;
import com.sandpolis.core.net.connection.ConnectionStore;

public class AgentConnectionRoutine extends InitTask {

	@Override
	public TaskOutcome run(TaskOutcome outcome) throws Exception {
		ConnectionStore.connect(config -> {
			config.address(CfgAgent.SERVER_ADDRESS.value().get());
			config.timeout = CfgAgent.SERVER_TIMEOUT.value().orElse(1000);
			config.bootstrap.handler(new ClientChannelInitializer(struct -> {
				struct.clientTlsInsecure();
			}));
		});

		return outcome.success();
	}

	@Override
	public String description() {
		return "Begin connection routine";
	}

}
