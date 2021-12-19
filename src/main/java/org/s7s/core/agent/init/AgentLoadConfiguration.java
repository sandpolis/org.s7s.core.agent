//============================================================================//
//                                                                            //
//            Copyright © 2015 - 2022 Sandpolis Software Foundation           //
//                                                                            //
//  This source file is subject to the terms of the Mozilla Public License    //
//  version 2. You may not use this file except in compliance with the MPLv2. //
//                                                                            //
//============================================================================//
package org.s7s.core.agent.init;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.s7s.core.agent.AgentConfig;
import org.s7s.core.agent.ConfigPrompter;
import org.s7s.core.instance.InitTask;

public class AgentLoadConfiguration extends InitTask {

	private static final Logger log = LoggerFactory.getLogger(AgentLoadConfiguration.class);

	@Override
	public TaskOutcome run(TaskOutcome.Factory outcome) throws Exception {

		// Check for configuration
		if (AgentConfig.EMBEDDED == null) {
			log.info("Requesting configuration via user input");
			try {
				new ConfigPrompter().run();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

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
