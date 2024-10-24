/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.geode.config.annotation;

import java.lang.annotation.Annotation;
import java.util.Optional;
import org.apache.geode.cache.client.ClientCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.gemfire.config.annotation.ClientCacheConfigurer;
import org.springframework.data.gemfire.config.annotation.support.AbstractAnnotationConfigSupport;
import org.springframework.util.StringUtils;

/**
 * The {@link DurableClientConfiguration} class is a Spring {@link Configuration} class used to configure
 * this {@link ClientCache} instance as a {@literal Durable Client} by setting the {@literal durable-client-id}
 * and {@literal durable-client-timeout} properties in addition to enabling {@literal keepAlive}
 * on {@link ClientCache} shutdown.
 *
 * @author John Blum
 * @see ClientCache
 * @see Bean
 * @see Configuration
 * @see ImportAware
 * @see AnnotationAttributes
 * @see AnnotationMetadata
 * @see ClientCacheConfigurer
 * @see AbstractAnnotationConfigSupport
 * @see EnableDurableClient
 * @since 1.0.0
 */
@Configuration
@SuppressWarnings("unused")
public class DurableClientConfiguration extends AbstractAnnotationConfigSupport implements ImportAware {

	public static final boolean DEFAULT_KEEP_ALIVE = true;
	public static final boolean DEFAULT_READY_FOR_EVENTS = true;

	public static final int DEFAULT_DURABLE_CLIENT_TIMEOUT = 300;

	private Boolean keepAlive = DEFAULT_KEEP_ALIVE;
	private Boolean readyForEvents = DEFAULT_READY_FOR_EVENTS;

	private Integer durableClientTimeout = DEFAULT_DURABLE_CLIENT_TIMEOUT;

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private String durableClientId;

	@Override
	protected Class<? extends Annotation> getAnnotationType() {
		return EnableDurableClient.class;
	}

	@Override
	@SuppressWarnings("all")
	public void setImportMetadata(AnnotationMetadata importMetadata) {

		if (isAnnotationPresent(importMetadata)) {

			AnnotationAttributes enableDurableClientAttributes = getAnnotationAttributes(importMetadata);

			this.durableClientId = enableDurableClientAttributes.containsKey("id")
				? enableDurableClientAttributes.getString("id")
				: null;

			this.durableClientTimeout = enableDurableClientAttributes.containsKey("timeout")
				? enableDurableClientAttributes.getNumber("timeout")
				: DEFAULT_DURABLE_CLIENT_TIMEOUT;

			this.keepAlive = enableDurableClientAttributes.containsKey("keepAlive")
				? enableDurableClientAttributes.getBoolean("keepAlive")
				: DEFAULT_KEEP_ALIVE;

			this.readyForEvents = enableDurableClientAttributes.containsKey("readyForEvents")
				? enableDurableClientAttributes.getBoolean("readyForEvents")
				: DEFAULT_READY_FOR_EVENTS;
		}
	}

	protected Optional<String> getDurableClientId() {

		return Optional.ofNullable(this.durableClientId)
			.filter(StringUtils::hasText);
	}

	protected Integer getDurableClientTimeout() {

		return this.durableClientTimeout != null
			? this.durableClientTimeout
			: DEFAULT_DURABLE_CLIENT_TIMEOUT;
	}

	protected Boolean getKeepAlive() {

		return this.keepAlive != null
			? this.keepAlive
			: DEFAULT_KEEP_ALIVE;
	}

	protected Boolean getReadyForEvents() {

		return this.readyForEvents != null
			? this.readyForEvents
			: DEFAULT_READY_FOR_EVENTS;
	}

	protected Logger getLogger() {
		return this.logger;
	}

	@Bean
	ClientCacheConfigurer clientCacheDurableClientConfigurer() {

		return (beanName, clientCacheFactoryBean) -> getDurableClientId().ifPresent(durableClientId -> {

			clientCacheFactoryBean.setDurableClientId(durableClientId);
			clientCacheFactoryBean.setDurableClientTimeout(getDurableClientTimeout());
			clientCacheFactoryBean.setKeepAlive(getKeepAlive());
			clientCacheFactoryBean.setReadyForEvents(getReadyForEvents());
		});
	}
}
