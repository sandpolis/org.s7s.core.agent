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

import static com.sandpolis.core.instance.profile.ProfileStore.ProfileStore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sandpolis.core.agent.bootagent.BootAgentUtil;
import com.sandpolis.core.instance.InitTask;
import com.sandpolis.core.instance.TaskOutcome;

public class AgentLocateBootAgents extends InitTask {

	private static final Logger log = LoggerFactory.getLogger(AgentLoadConfiguration.class);

	@Override
	public TaskOutcome run(TaskOutcome outcome) throws Exception {

		for (var partition : BootAgentUtil.findPartitions()) {
			var oid = ProfileStore.instance().oid().relative("bootagent/gpt_partition/uuid");
			ProfileStore.instance().attribute(oid).set(partition.unique_guid());
		}

		return outcome.success();
	}

	@Override
	public String description() {
		return "Locate boot agent partitions";
	}

	@Override
	public boolean fatal() {
		return false;
	}
}
