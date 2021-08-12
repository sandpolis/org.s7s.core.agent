//============================================================================//
//                                                                            //
//                         Copyright © 2015 Sandpolis                         //
//                                                                            //
//  This source file is subject to the terms of the Mozilla Public License    //
//  version 2. You may not use this file except in compliance with the MPL    //
//  as published by the Mozilla Foundation.                                   //
//                                                                            //
//============================================================================//
package com.sandpolis.core.agent.bootagent;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sandpolis.core.foundation.util.SystemUtil;

public final class BootAgentUtil {

	private static final Logger log = LoggerFactory.getLogger(BootAgentUtil.class);

	private static final String S7S_BOOT_AGENT_GUID = "94C553B9-68CC-4AB3-AF38-218A02D57675";

	private static final String ESP_GUID = "C12A7328-F81F-11D2-BA4B-00A0C93EC93B";

	public static GptPartition[] findPartitionCandidates() throws IOException {

		List<GptPartition> entries = new ArrayList<>();

		// TODO filter mounted partitions
		for (var partition : listGptPartitions()) {
			if (partition.type_guid().equalsIgnoreCase(S7S_BOOT_AGENT_GUID)) {
				continue;
			}

			if (partition.type_guid().equalsIgnoreCase(ESP_GUID)) {
				continue;
			}

			entries.add(partition);
		}

		return entries.toArray(GptPartition[]::new);
	}

	public static GptPartition[] findPartitions() throws IOException {

		List<GptPartition> entries = new ArrayList<>();

		for (var partition : listGptPartitions()) {
			if (partition.type_guid().equals(S7S_BOOT_AGENT_GUID.toString())) {
				entries.add(partition);
			}
		}

		return entries.toArray(GptPartition[]::new);
	}

	public static void launch(String uuid) throws IOException, InterruptedException {

		// Find the boot agent partition
		var partition = Arrays.stream(findPartitions()).filter(p -> p.unique_guid().equals(uuid)).findFirst();
		if (partition.isEmpty()) {
			throw new FileNotFoundException();
		}

		switch (SystemUtil.OS_TYPE) {
		case LINUX:
			// Check that EFI variables are available and accessible
			if (Files.list(Paths.get("/sys/firmware/efi/efivars")).count() == 0) {
				throw new RuntimeException();
			}

			// Create the boot entry (TODO manipulate the EFI vars without efibootmgr)
			if (SystemUtil.exec("efibootmgr", "-c", "-d", "/dev/", "-p", "", "-L", "S7S Boot Agent", "-l",
					"\\EFI\\s7s_x64.efi").exitValue() != 0) {
				throw new RuntimeException();
			}

			// If we made it this far, reboot in a background thread after returning success
			new Thread(() -> {
				try {
					Thread.sleep(8000);
					if (SystemUtil.exec("efibootmgr", "-n", "").exitValue() == 0) {
						SystemUtil.exec("reboot");
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}).start();
			break;
		case WINDOWS:
			break;
		default:
			break;
		}
	}

	public static void install(String uuid) throws IOException {

		// Find the candidate partition
		var partition = Arrays.stream(findPartitionCandidates()).filter(p -> p.unique_guid().equals(uuid)).findFirst();
		if (partition.isEmpty()) {
			throw new FileNotFoundException();
		}

		// TODO
	}

	private static int getLogicalBlockSize(Path device) {

		try {
			switch (SystemUtil.OS_TYPE) {
			case LINUX:
				return Integer.parseInt(Files
						.readString(Paths.get("/sys/block/" + device.getFileName() + "/queue/physical_block_size")));
			case WINDOWS:
				break;
			default:
				break;
			}
		} catch (Exception e) {
			return 512;
		}

		return 512;
	}

	/**
	 * List all storage devices currently attached to the system.
	 *
	 * @return A list of device paths
	 * @throws IOException
	 */
	private static Path[] listDevices() throws IOException {

		List<Path> paths = new ArrayList<>();

		switch (SystemUtil.OS_TYPE) {
		case LINUX:
			Files.list(Paths.get("/dev")).forEach(path -> {

				if (path.getFileName().toString().matches("^[sxv]d.$")) {
					paths.add(path);
				}

				if (path.getFileName().toString().matches("^nvme.n.$")) {
					paths.add(path);
				}
			});
			break;
		case WINDOWS:
			break;
		default:
			break;
		}

		return paths.toArray(Path[]::new);
	}

	/**
	 * List all GPT partitions on all devices currently attached to the system.
	 *
	 * @return A list of GPT partition descriptions
	 * @throws IOException
	 */
	private static GptPartition[] listGptPartitions() throws IOException {

		List<GptPartition> entries = new ArrayList<>();

		for (var path : listDevices()) {

			final int block_size = getLogicalBlockSize(path);

			log.trace("Discovered block size of {} for device: '{}'", block_size, path);

			var channel = FileChannel.open(path, StandardOpenOption.READ);

			// Skip protective MBR
			channel.position(512);

			var header = GptHeader.read(channel);

			for (int i = 0; i < header.number_of_entries(); i++) {
				channel.position(header.first_entry_lba() * 512 + (header.size_of_entry() * i));

				try {
					entries.add(GptPartition.read(channel));
				} catch (IOException e) {
				}
			}
		}

		return entries.toArray(GptPartition[]::new);
	}
}
