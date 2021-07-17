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
import java.nio.file.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sandpolis.core.agent.ConfigPrompter;
import com.sandpolis.core.agent.config.CfgAgent;
import com.sandpolis.core.foundation.config.CfgFoundation;
import com.sandpolis.core.instance.Environment;
import com.sandpolis.core.instance.InitTask;
import com.sandpolis.core.instance.TaskOutcome;

public class AgentLoadConfiguration extends InitTask {

	private static final Logger log = LoggerFactory.getLogger(AgentLoadConfiguration.class);

	@Override
	public TaskOutcome run(TaskOutcome outcome) throws Exception {
		if (CfgFoundation.DEVELOPMENT_MODE.value().orElse(false)) {
			CfgAgent.SERVER_ADDRESS.register("172.17.0.1");
			CfgAgent.SERVER_COOLDOWN.register(5000);
			CfgAgent.SERVER_TIMEOUT.register(5000);
		} else {

			// Check for configuration
			if (!Files.exists(Environment.CFG.path().resolve("config.properties"))) {
				log.info("Requesting configuration via user input");
				try {
					new ConfigPrompter().run();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}

			CfgAgent.SERVER_ADDRESS.require();
		}

		return outcome.success();
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
