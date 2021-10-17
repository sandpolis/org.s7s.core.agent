//============================================================================//
//                                                                            //
//                         Copyright © 2015 Sandpolis                         //
//                                                                            //
//  This source file is subject to the terms of the Mozilla Public License    //
//  version 2. You may not use this file except in compliance with the MPL    //
//  as published by the Mozilla Foundation.                                   //
//                                                                            //
//============================================================================//
package com.sandpolis.core.agent.exe;

import java.net.InetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.MessageLiteOrBuilder;
import com.sandpolis.core.serveragent.msg.MsgAgent.RQ_AgentMetadata;
import com.sandpolis.core.serveragent.msg.MsgAgent.RS_AgentMetadata;
import com.sandpolis.core.foundation.S7SSystem;
import com.sandpolis.core.instance.Environment;
import com.sandpolis.core.net.exelet.Exelet;

public final class AgentExe extends Exelet {

	private static final Logger log = LoggerFactory.getLogger(AgentExe.class);

	@Handler(auth = true)
	public static MessageLiteOrBuilder rq_agent_metadata(RQ_AgentMetadata rq) throws Exception {
		log.trace("rq_client_metadata");

		return RS_AgentMetadata.newBuilder()
				// Network hostname
				.setHostname(InetAddress.getLocalHost().getHostName())
				// OS Family
				.setOs(S7SSystem.OS_TYPE)
				// Base directory location
				.setInstallDirectory(Environment.JAR.path().getParent().toString());
	}

	private AgentExe() {
	}
}
