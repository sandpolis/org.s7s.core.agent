//============================================================================//
//                                                                            //
//            Copyright © 2015 - 2022 Sandpolis Software Foundation           //
//                                                                            //
//  This source file is subject to the terms of the Mozilla Public License    //
//  version 2. You may not use this file except in compliance with the MPLv2. //
//                                                                            //
//============================================================================//
package org.s7s.core.agent.init;

import static org.s7s.core.instance.plugin.PluginStore.PluginStore;
import static org.s7s.core.instance.profile.ProfileStore.ProfileStore;
import static org.s7s.core.instance.state.STStore.STStore;
import static org.s7s.core.instance.connection.ConnectionStore.ConnectionStore;
import static org.s7s.core.instance.exelet.ExeletStore.ExeletStore;
import static org.s7s.core.instance.network.NetworkStore.NetworkStore;
import static org.s7s.core.instance.stream.StreamStore.StreamStore;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executors;

import com.google.common.eventbus.Subscribe;
import org.s7s.core.agent.AgentConfig;
import org.s7s.core.agent.AgentContext;
import org.s7s.core.agent.cmd.AuthCmd;
import org.s7s.core.instance.plugin.PluginCmd;
import org.s7s.core.instance.Entrypoint;
import org.s7s.core.instance.InitTask;
import org.s7s.core.instance.InstanceContext;
import org.s7s.core.protocol.Stream.RQ_STStream;
import org.s7s.core.instance.plugin.PluginStore;
import org.s7s.core.instance.state.oid.Oid;
import org.s7s.core.instance.state.st.EphemeralDocument;
import org.s7s.core.instance.thread.ThreadStore;
import org.s7s.core.instance.channel.client.ClientChannelInitializer;
import org.s7s.core.instance.connection.ConnectionStore;
import org.s7s.core.instance.network.NetworkStore.ServerEstablishedEvent;
import org.s7s.core.instance.network.NetworkStore.ServerLostEvent;
import org.s7s.core.instance.state.STCmd;
import org.s7s.core.protocol.Session.RS_AuthSession;

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
			//config.exelets.add(AgentExe.class);
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
					config.address(AgentContext.SERVER_ADDRESS.get());
					config.timeout = AgentContext.SERVER_TIMEOUT.get();
					config.bootstrap.handler(new ClientChannelInitializer(struct -> {
						struct.clientTlsInsecure();
					}));
				});
			}

			@Subscribe
			private void onSrvEstablished(ServerEstablishedEvent event) {
				CompletionStage<RS_AuthSession> future;

				if (AgentContext.AUTH_PASSWORD.get() != null) {
					future = AuthCmd.async().target(event.sid()).password(AgentContext.AUTH_PASSWORD.get());
				} else if (AgentContext.AUTH_CERTIFICATE.get() != null) {
					// TODO
					return;
				} else {
					future = AuthCmd.async().target(event.sid()).none();
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

				if (InstanceContext.PLUGIN_ENABLED.get()) {
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
