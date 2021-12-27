//============================================================================//
//                                                                            //
//            Copyright © 2015 - 2022 Sandpolis Software Foundation           //
//                                                                            //
//  This source file is subject to the terms of the Mozilla Public License    //
//  version 2. You may not use this file except in compliance with the MPLv2. //
//                                                                            //
//============================================================================//
module org.s7s.core.agent {
	exports org.s7s.core.agent.init;
	exports org.s7s.core.agent.cmd;
	exports org.s7s.core.agent;

	opens org.s7s.core.agent;
	opens org.s7s.core.agent.init;

	requires org.s7s.core.foundation;
	requires org.s7s.core.instance;
	requires com.google.common;
	requires org.slf4j;
	requires com.google.protobuf;
	requires com.fasterxml.jackson.databind;
	requires org.s7s.core.integration.uefi;
}
