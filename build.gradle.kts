//============================================================================//
//                                                                            //
//            Copyright © 2015 - 2022 Sandpolis Software Foundation           //
//                                                                            //
//  This source file is subject to the terms of the Mozilla Public License    //
//  version 2. You may not use this file except in compliance with the MPLv2. //
//                                                                            //
//============================================================================//

plugins {
	id("java-library")
	id("org.s7s.build.module")
	id("org.s7s.build.protobuf")
	id("org.s7s.build.publish")
}

dependencies {
	testImplementation("org.junit.jupiter:junit-jupiter-api:5.+")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.+")

	if (project.getParent() == null) {
		api("org.s7s:core.instance:+")
		implementation("org.s7s:core.integration.uefi:+")
	} else {
		api(project(":core:org.s7s.core.instance"))
		implementation(project(":core:integration:org.s7s.core.integration.uefi"))
	}
}
