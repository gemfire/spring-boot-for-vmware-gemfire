/*
 * Copyright 2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.boot.autoconfigure.support;

import static org.assertj.core.api.Assertions.assertThat;
import org.apache.geode.cache.client.PoolManager;
import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("default-subscription-disabled-custom-enabled")
public class DefaultDisableCustomEnableSubscriptionWithNamedPropertyIntegrationTest extends AbstractDisableSubscriptionWithPropertyIntegrationTests {
  @Test
  public void customPoolSubscriptionDisabled() {
    assertThat(PoolManager.find("MyCustomPool").getSubscriptionEnabled()).isTrue();
  }
}
