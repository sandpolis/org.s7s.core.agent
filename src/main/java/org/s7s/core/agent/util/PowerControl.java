//============================================================================//
//                                                                            //
//            Copyright © 2015 - 2022 Sandpolis Software Foundation           //
//                                                                            //
//  This source file is subject to the terms of the Mozilla Public License    //
//  version 2. You may not use this file except in compliance with the MPLv2. //
//                                                                            //
//============================================================================//
package org.s7s.core.agent.util;

import org.s7s.core.foundation.S7SFile;
import org.s7s.core.foundation.S7SProcess;
import org.s7s.core.foundation.S7SSystem;

public final record PowerControl(boolean poweroffSupported, boolean rebootSupported, boolean suspendSupported,
		boolean hibernateSupported) {

	public static PowerControl of() {

		// Check container
		// TODO

		switch (S7SSystem.OS_TYPE) {
		case MACOS:
		case NETBSD:
		case OPENBSD:
		case SOLARIS:
		case DRAGONFLYBSD:
		case FREEBSD:
		case LINUX:

			// Check permissions
			// TODO

			if (S7SFile.which("systemctl").isPresent()) {
				return new PowerControl(true, true, true, true);
			}

			return new PowerControl(S7SFile.which("poweroff").isPresent(), S7SFile.which("reboot").isPresent(), false,
					false);
		case WINDOWS:
			return new PowerControl(true, true, true, true);
		default:
			return new PowerControl(false, false, false, false);
		}
	}

	/**
	 * Shut down the system.
	 */
	public void poweroff() {
		if (!poweroffSupported) {
			throw new IllegalStateException();
		}

		switch (S7SSystem.OS_TYPE) {
		case WINDOWS:
			S7SProcess.exec("shutdown", "/s").complete();
			break;
		default:
			S7SFile.which("systemctl").ifPresent(systemctl -> {
				S7SProcess.exec(systemctl.path(), "poweroff").complete();
			});
			S7SFile.which("poweroff").ifPresent(poweroff -> {
				S7SProcess.exec(poweroff.path()).complete();
			});
			break;
		}
	}

	/**
	 * Shut down and reboot (restart) the system.
	 */
	public void reboot() {
		if (!rebootSupported) {
			throw new IllegalStateException();
		}

		switch (S7SSystem.OS_TYPE) {
		case WINDOWS:
			S7SProcess.exec("shutdown", "/r").complete();
			break;
		default:
			S7SFile.which("systemctl").ifPresent(systemctl -> {
				S7SProcess.exec(systemctl.path(), "reboot").complete();
			});
			S7SFile.which("reboot").ifPresent(reboot -> {
				S7SProcess.exec(reboot.path()).complete();
			});
			break;
		}
	}
}
