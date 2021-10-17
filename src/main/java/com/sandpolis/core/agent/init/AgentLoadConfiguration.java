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

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sandpolis.core.agent.ConfigPrompter;
import com.sandpolis.core.agent.config.AgentConfig;
import com.sandpolis.core.agent.config.CfgAgent;
import com.sandpolis.core.instance.InitTask;

public class AgentLoadConfiguration extends InitTask {

	private static final Logger log = LoggerFactory.getLogger(AgentLoadConfiguration.class);

	@Override
	public TaskOutcome run(TaskOutcome.Factory outcome) throws Exception {

		// Check for configuration
		if (AgentConfig.get().isEmpty()) {
			log.info("Requesting configuration via user input");
			try {
				new ConfigPrompter().run();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		// Ensure config is valid
		CfgAgent.SERVER_ADDRESS.require();

		return outcome.succeeded();
	}

	@Override
	public String description() {
		return "Load agent configuration";
	}

	@Override
	public boolean fatal() {
		return true;
	}
}
