//============================================================================//
//                                                                            //
//            Copyright © 2015 - 2022 Sandpolis Software Foundation           //
//                                                                            //
//  This source file is subject to the terms of the Mozilla Public License    //
//  version 2. You may not use this file except in compliance with the MPLv2. //
//                                                                            //
//============================================================================//
package org.s7s.core.agent.exe;

import java.net.InetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.MessageLiteOrBuilder;
import org.s7s.core.foundation.S7SSystem;
import org.s7s.core.instance.exelet.Exelet;
import org.s7s.core.serveragent.Messages.RQ_AgentMetadata;
import org.s7s.core.serveragent.Messages.RS_AgentMetadata;

public final class AgentExe extends Exelet {

	private static final Logger log = LoggerFactory.getLogger(AgentExe.class);

	@Handler(auth = true)
	public static MessageLiteOrBuilder rq_agent_metadata(RQ_AgentMetadata rq) throws Exception {
		log.trace("rq_client_metadata");

		return RS_AgentMetadata.newBuilder()
				// Network hostname
				.setHostname(InetAddress.getLocalHost().getHostName())
				// OS Family
				.setOs(S7SSystem.OS_TYPE);
	}

	private AgentExe() {
	}
}
