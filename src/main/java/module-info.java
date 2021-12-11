//============================================================================//
//                                                                            //
//                         Copyright © 2015 Sandpolis                         //
//                                                                            //
//  This source file is subject to the terms of the Mozilla Public License    //
//  version 2. You may not use this file except in compliance with the MPL    //
//  as published by the Mozilla Foundation.                                   //
//                                                                            //
//============================================================================//
module com.sandpolis.core.agent {
	exports com.sandpolis.core.agent.init;
	exports com.sandpolis.core.agent.exe;
	exports com.sandpolis.core.agent.cmd;
	exports com.sandpolis.core.agent;

	opens com.sandpolis.core.agent;
	opens com.sandpolis.core.agent.init;

	requires com.sandpolis.core.foundation;
	requires com.sandpolis.core.instance;
	requires com.sandpolis.core.clientagent;
	requires com.sandpolis.core.serveragent;
	requires com.google.common;
	requires org.slf4j;
	requires com.google.protobuf;
	requires com.fasterxml.jackson.databind;
	requires com.sandpolis.core.integration.uefi;
}
