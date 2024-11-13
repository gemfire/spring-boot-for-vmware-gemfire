/*
 * Copyright 2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.boot.autoconfigure.support;

import static org.assertj.core.api.Assertions.assertThat;
import org.apache.geode.cache.client.PoolManager;
import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("subscription-disabled-unnamed")
public class DisableSubscriptionWithUnnamedPropertyIntegrationTest extends AbstractDisableSubscriptionWithPropertyIntegrationTests {
  @Test
  public void customPoolSubscriptionDisabled() {
    assertThat(PoolManager.find("MyCustomPool").getSubscriptionEnabled()).isFalse();
  }
}
