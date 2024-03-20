/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.geode.config.annotation;

import java.lang.annotation.Annotation;
import java.util.Optional;

import org.apache.geode.cache.Cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.gemfire.config.annotation.ClientCacheConfigurer;
import org.springframework.data.gemfire.config.annotation.PeerCacheConfigurer;
import org.springframework.data.gemfire.config.annotation.support.AbstractAnnotationConfigSupport;
import org.springframework.util.Assert;

/**
 * The {@link DistributedSystemIdConfiguration} class is a Spring {@link Configuration} class used to configure
 * the {@literal distributed-system-id} for a {@link Cache peer Cache member} in a cluster
 * when using the P2P topology.
 *
 * @author John Blum
 * @see Cache
 * @see Bean
 * @see Configuration
 * @see ImportAware
 * @see AnnotationAttributes
 * @see AnnotationMetadata
 * @see ClientCacheConfigurer
 * @see PeerCacheConfigurer
 * @see AbstractAnnotationConfigSupport
 * @see UseDistributedSystemId
 * @since 1.0.0
 */
@Configuration
@SuppressWarnings("unused")
public class DistributedSystemIdConfiguration extends AbstractAnnotationConfigSupport implements ImportAware {

	private static final String GEMFIRE_DISTRIBUTED_SYSTEM_ID_PROPERTY = "distributed-system-id";

	private Integer distributedSystemId;

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	protected Class<? extends Annotation> getAnnotationType() {
		return UseDistributedSystemId.class;
	}

	@Override
	@SuppressWarnings("all")
	public void setImportMetadata(AnnotationMetadata importMetadata) {

		if (isAnnotationPresent(importMetadata)) {

			AnnotationAttributes distributedSystemIdAttributes = getAnnotationAttributes(importMetadata);

			setDistributedSystemId(distributedSystemIdAttributes.containsKey("value")
				? distributedSystemIdAttributes.getNumber("value") : null);

			setDistributedSystemId(distributedSystemIdAttributes.containsKey("id")
				? distributedSystemIdAttributes.getNumber("id") : null);
		}
	}

	protected void setDistributedSystemId(Integer distributedSystemId) {

		this.distributedSystemId = Optional.ofNullable(distributedSystemId)
			.filter(id -> id > -1)
			.orElse(this.distributedSystemId);
	}

	protected Optional<Integer> getDistributedSystemId() {

		return Optional.ofNullable(this.distributedSystemId)
			.filter(id -> id > -1);
	}

	protected Logger getLogger() {
		return this.logger;
	}

	private int validateDistributedSystemId(int distributedSystemId) {

		Assert.isTrue(distributedSystemId >= -1 && distributedSystemId < 256,
			String.format("Distributed System ID [%d] must be between -1 and 255", distributedSystemId));

		return distributedSystemId;
	}

	@Bean
	ClientCacheConfigurer clientCacheDistributedSystemIdConfigurer() {

		return (beanName, clientCacheFactoryBean) -> getDistributedSystemId().ifPresent(distributedSystemId -> {

			Logger logger = getLogger();

			if (logger.isWarnEnabled()) {
				logger.warn("Distributed System Id [{}] was set on the ClientCache instance, which will not have any effect",
					distributedSystemId);
			}
		});
	}

	@Bean
	PeerCacheConfigurer peerCacheDistributedSystemIdConfigurer() {

		return (beanName, cacheFactoryBean) ->
			getDistributedSystemId().ifPresent(id -> cacheFactoryBean.getProperties()
				.setProperty(GEMFIRE_DISTRIBUTED_SYSTEM_ID_PROPERTY,
					String.valueOf(validateDistributedSystemId(id))));
	}
}
