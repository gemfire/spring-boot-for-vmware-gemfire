/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package example.app.geode.locator;

import org.apache.geode.distributed.Locator;
import org.apache.geode.distributed.LocatorLauncher;

/**
 * An Apache Geode {@link Locator} application configured and bootstrapped using the Apache Geode API.
 *
 * @author John Blum
 * @see org.apache.geode.distributed.Locator
 * @see org.apache.geode.distributed.LocatorLauncher
 * @since 1.3.2
 */
public class ApacheGeodeLocatorApplication {

	public static void main(String[] args) {

		LocatorLauncher locatorLauncher = new LocatorLauncher.Builder()
			.set("jmx-manager", "true")
			.set("jmx-manager-port", "0")
			.set("jmx-manager-start", "true")
			.setMemberName("ApacheGeodeBasedLocator")
			.setPort(0)
			.build();

		locatorLauncher.start();

		//locatorLauncher.waitOnLocator();
	}
}
