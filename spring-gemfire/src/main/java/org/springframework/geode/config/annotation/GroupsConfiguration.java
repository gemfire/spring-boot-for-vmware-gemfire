/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.config.annotation;

import java.lang.annotation.Annotation;
import java.util.Optional;
import org.apache.geode.cache.client.ClientCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.gemfire.client.ClientCacheFactoryBean;
import org.springframework.data.gemfire.config.annotation.ClientCacheConfigurer;
import org.springframework.data.gemfire.config.annotation.support.AbstractAnnotationConfigSupport;
import org.springframework.util.StringUtils;

/**
 * The {@link GroupsConfiguration} class is a Spring {@link Configuration} class used to configure the {@literal groups}
 * in which a client belongs in a GemFire distributed system.
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
 * @see UseGroups
 * @since 1.0.0
 */
@Configuration
@SuppressWarnings("unused")
public class GroupsConfiguration extends AbstractAnnotationConfigSupport implements ImportAware {

	private static final String GEMFIRE_GROUPS_PROPERTY = "groups";

	private String[] groups = {};

	@Override
	protected Class<? extends Annotation> getAnnotationType() {
		return UseGroups.class;
	}

	@Override
	public void setImportMetadata(AnnotationMetadata importMetadata) {

		if (isAnnotationPresent(importMetadata)) {

			AnnotationAttributes inGroupsAttributes = getAnnotationAttributes(importMetadata);

			setGroups(inGroupsAttributes.containsKey("value")
				? inGroupsAttributes.getStringArray("value") : null);

			setGroups(inGroupsAttributes.containsKey("groups")
				? inGroupsAttributes.getStringArray("groups") : null);
		}
	}

	protected void setGroups(String[] groups) {

		this.groups = Optional.ofNullable(groups)
			.filter(it -> it.length > 0)
			.orElse(this.groups);
	}

	protected Optional<String[]> getGroups() {

		return Optional.ofNullable(this.groups)
			.filter(it -> it.length > 0);
	}

	@Bean
	ClientCacheConfigurer clientCacheGroupsConfigurer() {
		return (beaName, clientCacheFactoryBean) -> configureGroups(clientCacheFactoryBean);
	}

	private void configureGroups(ClientCacheFactoryBean cacheFactoryBean) {
		getGroups().ifPresent(groups -> cacheFactoryBean.getProperties()
			.setProperty(GEMFIRE_GROUPS_PROPERTY, StringUtils.arrayToCommaDelimitedString(groups)));
	}
}
