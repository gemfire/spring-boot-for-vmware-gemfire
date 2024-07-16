/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.config.annotation;

import java.util.Set;
import org.slf4j.Logger;
import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.boot.cloud.CloudPlatform;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.data.gemfire.support.ConnectionEndpointList;
import org.springframework.data.gemfire.util.CollectionUtils;
import org.springframework.lang.NonNull;

/**
 * The {@link ClusterAvailableConfiguration} class is a Spring {@link Configuration} class that connects to a cluster
 * of servers when available.
 *
 * @author John Blum
 * @see AnyNestedCondition
 * @see org.springframework.boot.autoconfigure.condition.ConditionalOnCloudPlatform
 * @see CloudPlatform
 * @see ConditionContext
 * @see Conditional
 * @see Configuration
 * @see Environment
 * @see AnnotatedTypeMetadata
 * @see ClusterAwareConfiguration
 * @since 1.2.0
 */
@Configuration
@Conditional(ClusterAvailableConfiguration.AnyClusterAvailableCondition.class)
@SuppressWarnings("unused")
public class ClusterAvailableConfiguration {

	private static final Set<CloudPlatform> SUPPORTED_CLOUD_PLATFORMS =
		CollectionUtils.asSet(CloudPlatform.CLOUD_FOUNDRY, CloudPlatform.KUBERNETES);

	public static final class AnyClusterAvailableCondition extends AnyNestedCondition {

		public AnyClusterAvailableCondition() {
			super(ConfigurationPhase.PARSE_CONFIGURATION);
		}

		//@ConditionalOnCloudPlatform(CloudPlatform.CLOUD_FOUNDRY)
		@Conditional(CloudFoundryClusterAvailableCondition.class)
		static class IsCloudFoundryClusterAvailableCondition { }

		//@ConditionalOnCloudPlatform(CloudPlatform.KUBERNETES)
		@Conditional(KubernetesClusterAvailableCondition.class)
		static class IsKubernetesClusterAvailableCondition { }

		@Conditional(StandaloneClusterAvailableCondition.class)
		static class IsStandaloneClusterAvailableCondition { }

	}

	protected static abstract class AbstractCloudPlatformAvailableCondition
			extends ClusterAwareConfiguration.ClusterAwareCondition {

		protected abstract String getCloudPlatformName();

		@Override
		protected String getRuntimeEnvironmentName() {
			return getCloudPlatformName();
		}

		protected abstract boolean isCloudPlatformActive(@NonNull Environment environment);

		protected boolean isInfoLoggingEnabled() {
			return getLogger().isInfoEnabled();
		}

		protected boolean isMatchingLoggable(boolean match) {
			return match && isInfoLoggingEnabled();
		}

		@Override
		public synchronized final boolean matches(@NonNull ConditionContext conditionContext,
				@NonNull AnnotatedTypeMetadata typeMetadata) {

			boolean match = isCloudPlatformActive(conditionContext.getEnvironment());

			if (isMatchingLoggable(match)) {
				match |= super.matches(conditionContext, typeMetadata);
			}

			if (match && !wasClusterAvailabilityEvaluated()) {
				set(true);
			}

			return match;
		}

		@Override
		protected void logConnectedRuntimeEnvironment(@NonNull Logger logger) {

			if (logger.isInfoEnabled()) {
				logger.info("Spring Boot application is running in a client/server topology,"
					+ " inside a [{}] Cloud-managed Environment", getRuntimeEnvironmentName());
			}
		}

		@Override
		protected void logUnconnectedRuntimeEnvironment(@NonNull Logger logger) {

			if (logger.isInfoEnabled()) {
				logger.info("No cluster was found; Spring Boot application is running in a [{}]"
						+ " Cloud-managed Environment", getRuntimeEnvironmentName());
			}
		}

		@Override
		protected void configureTopology(@NonNull Environment environment,
				@NonNull ConnectionEndpointList connectionEndpoints, int connectionCount) {
			// do nothing!
		}
	}

	public static class CloudFoundryClusterAvailableCondition extends AbstractCloudPlatformAvailableCondition {

		protected static final String CLOUD_FOUNDRY_NAME = "CloudFoundry";
		protected static final String RUNTIME_ENVIRONMENT_NAME = "VMware Tanzu GemFire for VMs";

		@Override
		protected String getCloudPlatformName() {
			return CLOUD_FOUNDRY_NAME;
		}

		@Override
		protected String getRuntimeEnvironmentName() {
			return RUNTIME_ENVIRONMENT_NAME;
		}

		@Override
		protected boolean isCloudPlatformActive(@NonNull Environment environment) {
			return environment != null && CloudPlatform.CLOUD_FOUNDRY.isActive(environment);
		}
	}

	public static class KubernetesClusterAvailableCondition extends AbstractCloudPlatformAvailableCondition {

		protected static final String KUBERNETES_NAME = "Kubernetes";
		protected static final String RUNTIME_ENVIRONMENT_NAME = "VMware Tanzu GemFire for K8S";

		@Override
		protected String getCloudPlatformName() {
			return KUBERNETES_NAME;
		}

		@Override
		protected String getRuntimeEnvironmentName() {
			return RUNTIME_ENVIRONMENT_NAME;
		}

		@Override
		protected boolean isCloudPlatformActive(@NonNull Environment environment) {
			return environment != null && CloudPlatform.KUBERNETES.isActive(environment);
		}
	}

	public static class StandaloneClusterAvailableCondition
			extends ClusterAwareConfiguration.ClusterAwareCondition {

		@Override
		public synchronized boolean matches(@NonNull ConditionContext conditionContext,
				@NonNull AnnotatedTypeMetadata typeMetadata) {

			return isNotSupportedCloudPlatform(conditionContext)
				&& super.matches(conditionContext, typeMetadata);
		}

		private boolean isNotSupportedCloudPlatform(@NonNull ConditionContext conditionContext) {
			return conditionContext != null && isNotSupportedCloudPlatform(conditionContext.getEnvironment());
		}

		private boolean isNotSupportedCloudPlatform(@NonNull Environment environment) {

			CloudPlatform activeCloudPlatform = environment != null
				? CloudPlatform.getActive(environment)
				: null;

			return !isSupportedCloudPlatform(activeCloudPlatform);
		}

		private boolean isSupportedCloudPlatform(@NonNull CloudPlatform cloudPlatform) {
			return cloudPlatform != null && SUPPORTED_CLOUD_PLATFORMS.contains(cloudPlatform);
		}
	}
}
