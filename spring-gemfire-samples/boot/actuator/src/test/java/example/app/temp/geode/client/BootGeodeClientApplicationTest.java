/*
 * Copyright 2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package example.app.temp.geode.client;

import static org.assertj.core.api.Assertions.assertThat;
import com.vmware.gemfire.testcontainers.GemFireCluster;
import example.app.temp.model.TemperatureReading;
import java.io.IOException;
import java.time.Duration;
import org.apache.geode.cache.Region;
import org.awaitility.Awaitility;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = BootGeodeClientApplication.class)
public class BootGeodeClientApplicationTest {

  private static GemFireCluster gemFireCluster;

  @BeforeClass
  public static void runGemFireServer() throws IOException {
    String dockerImage = System.getProperty("spring.test.gemfire.docker.image");
    gemFireCluster = new GemFireCluster(dockerImage, 1, 1)
        .withGfsh(false, "create region --name=TemperatureReadings --type=PARTITION");

    gemFireCluster.acceptLicense().start();

    System.setProperty("spring.data.gemfire.pool.servers", "localhost[" + gemFireCluster.getServerPorts().get(0) + "]");
  }

  @Autowired
  @Qualifier("TemperatureReadings")
  Region<Long, TemperatureReading> temperatureReadings;

  @Test
  public void testTemperatureReadings() {
    Awaitility.await().atMost(Duration.ofMinutes(1)).untilAsserted(() ->
        assertThat(temperatureReadings.sizeOnServer()).isGreaterThan(10)
    );
  }
}
