/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.config.annotation;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.apache.geode.cache.client.ClientCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.gemfire.client.ClientCacheFactoryBean;
import org.springframework.data.gemfire.config.annotation.ClientCacheConfigurer;
import org.springframework.data.gemfire.config.annotation.support.AbstractAnnotationConfigSupport;
import org.springframework.util.StringUtils;

/**
 * The {@link MemberNameConfiguration} class is a Spring {@link Configuration} class used to configure an Apache Geode
 * member name in the distributed system, whether the member is a {@link ClientCache client} in the client/server
 * topology or a Cache peer in a cluster using the P2P topology.
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
 * @see UseMemberName
 * @since 1.0.0
 */
@Configuration
@SuppressWarnings("unused")
public class MemberNameConfiguration extends AbstractAnnotationConfigSupport implements ImportAware  {

	private static final String GEMFIRE_NAME_PROPERTY = "name";
	private static final String SPRING_APPLICATION_NAME_PROPERTY = "spring.application.name";
	private static final String SPRING_DATA_GEMFIRE_CACHE_NAME_PROPERTY = "spring.data.gemfire.cache.name";
	private static final String SPRING_DATA_GEMFIRE_NAME_PROPERTY = "spring.data.gemfire.name";
	private static final String SPRING_DATA_GEODE_CACHE_NAME_PROPERTY = "spring.data.geode.cache.name";
	private static final String SPRING_DATA_GEODE_NAME_PROPERTY = "spring.data.geode.name";

	private static final Set<String> NAME_PROPERTIES = new HashSet<>();

	static {
		NAME_PROPERTIES.add(SPRING_APPLICATION_NAME_PROPERTY);
		NAME_PROPERTIES.add(SPRING_DATA_GEMFIRE_CACHE_NAME_PROPERTY);
		NAME_PROPERTIES.add(SPRING_DATA_GEMFIRE_NAME_PROPERTY);
		NAME_PROPERTIES.add(SPRING_DATA_GEODE_CACHE_NAME_PROPERTY);
		NAME_PROPERTIES.add(SPRING_DATA_GEODE_NAME_PROPERTY);
	}

	private String memberName;

	@Override
	protected Class<? extends Annotation> getAnnotationType() {
		return UseMemberName.class;
	}

	@Override
	@SuppressWarnings("all")
	public void setImportMetadata(AnnotationMetadata importMetadata) {

		if (isAnnotationPresent(importMetadata)) {

			AnnotationAttributes memberNameAttributes = getAnnotationAttributes(importMetadata);

			setMemberName(memberNameAttributes.containsKey("value")
				? memberNameAttributes.getString("value") : null);

			setMemberName(memberNameAttributes.containsKey("name")
				? memberNameAttributes.getString("name") : null);
		}
	}

	protected void setMemberName(String memberName) {

		this.memberName = Optional.ofNullable(memberName)
			.filter(StringUtils::hasText)
			.orElse(this.memberName);
	}

	protected Optional<String> getMemberName() {

		return Optional.ofNullable(this.memberName)
			.filter(StringUtils::hasText);
	}

	@Bean
	@Order(Ordered.HIGHEST_PRECEDENCE) // apply first (e.g. before CacheNameAutoConfiguration)
	ClientCacheConfigurer clientCacheMemberNameConfigurer(Environment environment) {
		return (beanName, clientCacheFactoryBean) -> configureMemberName(environment, clientCacheFactoryBean);
	}
	
	private void configureMemberName(Environment environment, ClientCacheFactoryBean cacheFactoryBean) {

		getMemberName()
			.filter(memberName -> namePropertiesNotPresent(environment))
			.ifPresent(memberName ->
				cacheFactoryBean.getProperties().setProperty(GEMFIRE_NAME_PROPERTY, memberName));
	}

	private boolean namePropertiesArePresent(Environment environment) {

		return NAME_PROPERTIES.stream()
			.anyMatch(environment::containsProperty);
	}

	private boolean namePropertiesNotPresent(Environment environment) {
		return !namePropertiesArePresent(environment);
	}
}
