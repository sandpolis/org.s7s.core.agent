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

import static com.sandpolis.core.instance.plugin.PluginStore.PluginStore;
import static com.sandpolis.core.instance.profile.ProfileStore.ProfileStore;
import static com.sandpolis.core.instance.state.STStore.STStore;
import static com.sandpolis.core.net.connection.ConnectionStore.ConnectionStore;
import static com.sandpolis.core.net.exelet.ExeletStore.ExeletStore;
import static com.sandpolis.core.net.network.NetworkStore.NetworkStore;
import static com.sandpolis.core.net.stream.StreamStore.StreamStore;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executors;

import com.google.common.eventbus.Subscribe;
import com.sandpolis.core.agent.cmd.AuthCmd;
import com.sandpolis.core.agent.config.AgentConfig;
import com.sandpolis.core.agent.config.CfgAgent;
import com.sandpolis.core.agent.exe.AgentExe;
import com.sandpolis.core.clientagent.cmd.PluginCmd;
import com.sandpolis.core.instance.Entrypoint;
import com.sandpolis.core.instance.InitTask;
import com.sandpolis.core.instance.config.CfgInstance;
import com.sandpolis.core.instance.config.InstanceConfig;
import com.sandpolis.core.instance.Messages.RQ_STStream;
import com.sandpolis.core.instance.state.oid.Oid;
import com.sandpolis.core.instance.state.st.EphemeralDocument;
import com.sandpolis.core.instance.thread.ThreadStore;
import com.sandpolis.core.net.channel.client.ClientChannelInitializer;
import com.sandpolis.core.net.network.NetworkStore.ServerEstablishedEvent;
import com.sandpolis.core.net.network.NetworkStore.ServerLostEvent;
import com.sandpolis.core.net.state.STCmd;
import com.sandpolis.core.serveragent.Messages.RS_AuthSession;

import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.UnorderedThreadPoolEventExecutor;

public class AgentLoadStores extends InitTask {

	@Override
	public TaskOutcome run(TaskOutcome.Factory outcome) throws Exception {
		ThreadStore.ThreadStore.init(config -> {
			config.defaults.put("net.exelet", new NioEventLoopGroup(2).next());
			config.defaults.put("net.connection.outgoing", new NioEventLoopGroup(2).next());
			config.defaults.put("net.connection.loop", new NioEventLoopGroup(2).next());
			config.defaults.put("net.message.incoming", new UnorderedThreadPoolEventExecutor(2));
			config.defaults.put("store.event_bus", Executors.newSingleThreadExecutor());
			config.defaults.put("attributes", Executors.newScheduledThreadPool(1));
		});

		STStore.init(config -> {
			config.concurrency = 1;
			config.root = new EphemeralDocument(null, null);
		});

		ProfileStore.init(config -> {
			config.collection = STStore.get(Oid.of("/profile"));
		});

		PluginStore.init(config -> {
			config.collection = STStore.get(Oid.of("/profile/*/plugin", Entrypoint.data().uuid()));
		});

		StreamStore.init(config -> {
		});

		ExeletStore.init(config -> {
			config.exelets.add(AgentExe.class);
		});

		ConnectionStore.init(config -> {
			config.collection = STStore.get(Oid.of("/connection"));
		});

		NetworkStore.init(config -> {
			config.collection = STStore.get(Oid.of("/network_connection"));
		});

		NetworkStore.register(new Object() {
			@Subscribe
			private void onSrvLost(ServerLostEvent event) {

				// Intentionally wait before reconnecting
				// TODO don't block
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {

				}

				ConnectionStore.connect(config -> {
					config.address(AgentConfig.SERVER_ADDRESS.value().get());
					config.timeout = AgentConfig.SERVER_TIMEOUT.value().orElse(1000);
					config.bootstrap.handler(new ClientChannelInitializer(struct -> {
						struct.clientTlsInsecure();
					}));
				});
			}

			@Subscribe
			private void onSrvEstablished(ServerEstablishedEvent event) {
				CompletionStage<RS_AuthSession> future;

				switch (CfgAgent.AUTH_TYPE.value().orElse("none")) {
				case "password":
					future = AuthCmd.async().target(event.sid()).password(AgentConfig.AUTH_PASSWORD.value().get());
					break;
				default:
					future = AuthCmd.async().target(event.sid()).none();
					break;
				}

				future = future.thenApply(rs -> {
					switch (rs) {
					case AUTH_SESSION_OK:
						break;
					default:
						// Close the connection
						ConnectionStore.getBySid(event.sid()).ifPresent(sock -> {
							sock.close();
						});
						break;
					}
					return rs;
				});

				if (InstanceConfig.PLUGIN_ENABLED.value().orElse(true)) {
					future.thenAccept(rs -> {
						switch (rs) {
						case AUTH_SESSION_OK:
							// Synchronize plugins
							PluginCmd.async().synchronize().thenRun(PluginStore::loadPlugins);
							break;
						default:
							break;
						}
					});
				}

				// Sync the instance profile to the server
				STCmd.async().sync(ProfileStore.instance().oid(), config -> {
					config.direction = RQ_STStream.Direction.UPSTREAM;
					config.initiator = true;
					config.connection = ConnectionStore.getBySid(NetworkStore.getPreferredServer().orElse(0))
							.orElse(null);
				});
			}
		});

		return outcome.succeeded();
	}

	@Override
	public String description() {
		return "Load static stores";
	}

}
