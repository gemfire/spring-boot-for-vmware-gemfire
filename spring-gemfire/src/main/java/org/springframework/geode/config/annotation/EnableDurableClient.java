/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.geode.config.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.geode.cache.client.ClientCache;
import org.springframework.context.annotation.Import;

/**
 * The {@link EnableDurableClient} annotation configures a {@link ClientCache} instance as a {@literal Durable Client}.
 *
 * @author John Blum
 * @see Documented
 * @see Inherited
 * @see Retention
 * @see Target
 * @see ClientCache
 * @see Import
 * @see DurableClientConfiguration
 * @since 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Import(DurableClientConfiguration.class)
@SuppressWarnings("unused")
public @interface EnableDurableClient {

	/**
	 * Used only for clients in a client/server installation. If set, this indicates that the client is durable
	 * and identifies the client. The ID is used by servers to reestablish any messaging that was interrupted
	 * by client downtime.
	 */
	String id();

	/**
	 * Configure whether the server should keep the durable client's queues alive for the timeout period.
	 *
	 * Defaults to {@literal true}.
	 */
	boolean keepAlive() default DurableClientConfiguration.DEFAULT_KEEP_ALIVE;

	/**
	 * Configures whether the {@link ClientCache} is ready to recieve events on startup.
	 *
	 * Defaults to {@literal true}.
	 */
	boolean readyForEvents() default DurableClientConfiguration.DEFAULT_READY_FOR_EVENTS;

	/**
	 * Used only for clients in a client/server installation. Number of seconds this client can remain disconnected
	 * from its server and have the server continue to accumulate durable events for it.
	 *
	 * Defaults to {@literal 300 seconds}, or {@literal 5 minutes}.
	 */
	int timeout() default DurableClientConfiguration.DEFAULT_DURABLE_CLIENT_TIMEOUT;

}
