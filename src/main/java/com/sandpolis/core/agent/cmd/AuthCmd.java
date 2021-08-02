//============================================================================//
//                                                                            //
//                         Copyright © 2015 Sandpolis                         //
//                                                                            //
//  This source file is subject to the terms of the Mozilla Public License    //
//  version 2. You may not use this file except in compliance with the MPL    //
//  as published by the Mozilla Foundation.                                   //
//                                                                            //
//============================================================================//
package com.sandpolis.core.agent.cmd;

import java.util.concurrent.CompletionStage;

import com.sandpolis.core.serveragent.msg.MsgAuth.RQ_AuthSession;
import com.sandpolis.core.foundation.Result.Outcome;
import com.sandpolis.core.net.cmdlet.Cmdlet;

/**
 * {@link AuthCmd} contains commands required for agent instances to
 * authenticate with a server.
 *
 * @since 5.0.0
 */
public final class AuthCmd extends Cmdlet<AuthCmd> {

	/**
	 * Attempt to authenticate without providing any form of identification.
	 *
	 * @return An asynchronous {@link CompletionStage}
	 */
	public CompletionStage<Outcome> none() {

		return request(Outcome.class, RQ_AuthSession.newBuilder()).thenApply(rs -> {
			if (rs.getResult()) {
				target.authenticate();
			}
			return rs;
		});
	}

	/**
	 * Attempt to authenticate with a password.
	 *
	 * @return An asynchronous {@link CompletionStage}
	 */
	public CompletionStage<Outcome> password(String password) {

		return request(Outcome.class, RQ_AuthSession.newBuilder().setPassword(password)).thenApply(rs -> {
			if (rs.getResult()) {
				target.authenticate();
			}
			return rs;
		});
	}

	/**
	 * Prepare for an asynchronous command.
	 *
	 * @return A configurable object from which all asynchronous (nonstatic)
	 *         commands in {@link AuthCmd} can be invoked
	 */
	public static AuthCmd async() {
		return new AuthCmd();
	}

	private AuthCmd() {
	}
}
