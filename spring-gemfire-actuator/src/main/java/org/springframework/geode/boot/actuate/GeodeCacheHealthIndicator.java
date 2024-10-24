/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.boot.actuate;

import java.net.URL;
import java.util.function.Function;
import org.apache.geode.CancelCriterion;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.control.ResourceManager;
import org.apache.geode.distributed.DistributedMember;
import org.apache.geode.distributed.DistributedSystem;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.data.gemfire.util.CollectionUtils;
import org.springframework.geode.boot.actuate.health.AbstractGeodeHealthIndicator;
import org.springframework.util.StringUtils;

/**
 * The {@link GeodeCacheHealthIndicator} class is a Spring Boot {@link HealthIndicator} providing details about
 * the health of the {@link ClientCache}, the {@link DistributedSystem}, this {@link DistributedMember}
 * and the {@link ResourceManager}.
 *
 * @author John Blum
 * @see java.net.URL
 * @see java.util.Optional
 * @see java.util.function.Function
 * @see org.apache.geode.cache.client.ClientCache
 * @see org.apache.geode.cache.control.ResourceManager
 * @see org.apache.geode.distributed.DistributedMember
 * @see org.apache.geode.distributed.DistributedSystem
 * @see org.springframework.boot.actuate.health.Health
 * @see org.springframework.boot.actuate.health.HealthIndicator
 * @see org.springframework.geode.boot.actuate.health.AbstractGeodeHealthIndicator
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public class GeodeCacheHealthIndicator extends AbstractGeodeHealthIndicator {

	private final Function<Health.Builder, Health.Builder> gemfireHealthIndicatorFunctions = withCacheDetails()
		.andThen(withDistributedSystemDetails())
		.andThen(withDistributedMemberDetails())
		.andThen(withResourceManagerDetails());

	/**
	 * Default constructor to construct an uninitialized instance of {@link GeodeCacheHealthIndicator},
	 * which will not provide any health information.
	 */
	public GeodeCacheHealthIndicator() {
		super("(Client) Cache health check failed");
	}

	/**
	 * Constructs an instance of the {@link GeodeCacheHealthIndicator} initialized with a reference to
	 * the {@link ClientCache} instance.
	 *
	 * @param gemfireCache reference to the {@link ClientCache} instance used to collect health information.
	 * @throws IllegalArgumentException if {@link ClientCache} is {@literal null}.
	 * @see org.apache.geode.cache.client.ClientCache
	 */
	public GeodeCacheHealthIndicator(ClientCache gemfireCache) {
		super(gemfireCache);
	}

	/**
	 * Return a collection of {@link Function Functions} that apply {@link HealthIndicator} information
	 * about the {@link ClientCache} to the {@link Health} aggregate object.
	 *
	 * @return a collection of {@link Function Functions} applying {@link HealthIndicator} information
	 * about the {@link ClientCache} to a {@link Health} object.
	 * @see org.springframework.boot.actuate.health.Health.Builder
	 * @see java.util.function.Function
	 */
	protected Function<Health.Builder, Health.Builder> getGemfireHealthIndicatorFunctions() {
		return this.gemfireHealthIndicatorFunctions;
	}

	@Override
	protected void doHealthCheck(Health.Builder builder) {

		if (getGemFireCache().isPresent()) {

			getGemfireHealthIndicatorFunctions().apply(builder);

			builder.status(getGemFireCache().map(ClientCache::isClosed).orElse(true)
				? Status.DOWN : Status.UP);

			return;
		}

		builder.unknown();
	}

	private Function<Health.Builder, Health.Builder> withCacheDetails() {

		return healthBuilder -> healthBuilder.withDetail("geode.cache.name", getGemFireCache().map(ClientCache::getName).orElse(""))
				.withDetail("geode.cache.closed", getGemFireCache().map(ClientCache::isClosed).map(this::toYesNoString).orElse("Yes"))
			.withDetail("geode.cache.cancel-in-progress", getGemFireCache()
				.map(ClientCache::getCancelCriterion)
				.filter(CancelCriterion::isCancelInProgress)
				.isPresent() ? "Yes" : "No");
	}

	private Function<Health.Builder, Health.Builder> withDistributedMemberDetails() {

		return healthBuilder -> getGemFireCache()
			.map(ClientCache::getDistributedSystem)
			.map(DistributedSystem::getDistributedMember)
			.map(distributedMember -> healthBuilder
				.withDetail("geode.distributed-member.id", distributedMember.getId())
				.withDetail("geode.distributed-member.name", distributedMember.getName())
				.withDetail("geode.distributed-member.groups", distributedMember.getGroups())
				.withDetail("geode.distributed-member.host", distributedMember.getHost())
				.withDetail("geode.distributed-member.process-id", distributedMember.getProcessId())
			)
			.orElse(healthBuilder);
	}

	private Function<Health.Builder, Health.Builder> withDistributedSystemDetails() {

		return healthBuilder -> getGemFireCache()
			.map(ClientCache::getDistributedSystem)
			.map(distributedSystem -> healthBuilder
				.withDetail("geode.distributed-system.member-count", toMemberCount(distributedSystem))
				.withDetail("geode.distributed-system.connection", toConnectedNoConnectedString(distributedSystem.isConnected()))
				.withDetail("geode.distributed-system.reconnecting", toYesNoString(distributedSystem.isReconnecting()))
				.withDetail("geode.distributed-system.properties-location", toString(DistributedSystem.getPropertiesFileURL()))
				.withDetail("geode.distributed-system.security-properties-location", toString(DistributedSystem.getSecurityPropertiesFileURL()))
			)
			.orElse(healthBuilder);
	}

	private Function<Health.Builder, Health.Builder> withResourceManagerDetails() {

		return healthBuilder -> getGemFireCache()
			.map(ClientCache::getResourceManager)
			.map(resourceManager -> healthBuilder
				.withDetail("geode.resource-manager.critical-heap-percentage", resourceManager.getCriticalHeapPercentage())
				.withDetail("geode.resource-manager.critical-off-heap-percentage", resourceManager.getCriticalOffHeapPercentage())
				.withDetail("geode.resource-manager.eviction-heap-percentage", resourceManager.getEvictionHeapPercentage())
				.withDetail("geode.resource-manager.eviction-off-heap-percentage", resourceManager.getEvictionOffHeapPercentage())
			)
			.orElse(healthBuilder);
	}

	private String emptyIfUnset(String value) {
		return StringUtils.hasText(value) ? value : "";
	}

	private String toConnectedNoConnectedString(Boolean connected) {
		return Boolean.TRUE.equals(connected) ? "Connected" : "Not Connected";
	}

	private int toMemberCount(DistributedSystem distributedSystem) {
		return CollectionUtils.nullSafeSize(distributedSystem.getAllOtherMembers()) + 1;
	}

	private String toString(URL url) {

		String urlString = url != null ? url.toExternalForm() : null;

		return emptyIfUnset(urlString);
	}
}
