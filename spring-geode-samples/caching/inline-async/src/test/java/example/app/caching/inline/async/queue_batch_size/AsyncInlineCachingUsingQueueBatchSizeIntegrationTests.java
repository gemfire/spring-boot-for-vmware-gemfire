/*
 * Copyright (c) VMware, Inc. 2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package example.app.caching.inline.async.queue_batch_size;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.data.gemfire.util.RuntimeExceptionFactory.newIllegalStateException;

import java.time.Duration;
import java.util.Optional;

import jakarta.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.asyncqueue.AsyncEventListener;
import org.apache.geode.cache.asyncqueue.AsyncEventQueue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.gemfire.GemfireTemplate;
import org.springframework.data.gemfire.config.annotation.PeerCacheApplication;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.util.ReflectionUtils;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.geode.cache.RepositoryAsyncEventListener;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import org.awaitility.Awaitility;

import example.app.caching.inline.async.client.model.Golfer;
import example.app.caching.inline.async.client.repo.GolferRepository;
import example.app.caching.inline.async.client.service.GolferService;
import example.app.caching.inline.async.config.AsyncInlineCachingConfiguration;
import example.app.caching.inline.async.config.AsyncInlineCachingRegionConfiguration;

/**
 * Integration Tests for Spring Boot configured Async Inline Caching with Apache Geode using Queue Batch Size.
 *
 * @author John Blum
 * @see java.time.Duration
 * @see org.junit.Test
 * @see org.apache.geode.cache.Cache
 * @see org.apache.geode.cache.Region
 * @see org.apache.geode.cache.asyncqueue.AsyncEventListener
 * @see org.apache.geode.cache.asyncqueue.AsyncEventQueue
 * @see org.awaitility.Awaitility
 * @see org.springframework.boot.autoconfigure.EnableAutoConfiguration
 * @see org.springframework.boot.autoconfigure.domain.EntityScan
 * @see org.springframework.boot.test.context.SpringBootTest
 * @see org.springframework.context.annotation.Bean
 * @see org.springframework.context.annotation.Import
 * @see org.springframework.data.gemfire.config.annotation.PeerCacheApplication
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.data.jpa.repository.config.EnableJpaRepositories
 * @see org.springframework.geode.cache.RepositoryAsyncEventListener
 * @see org.springframework.test.context.ActiveProfiles
 * @see org.springframework.test.context.junit4.SpringRunner
 * @see example.app.caching.inline.async.config.AsyncInlineCachingConfiguration
 * @since 1.4.0
 */
@RunWith(SpringRunner.class)
@ActiveProfiles("queue-batch-size")
@SpringBootTest(
	properties = {
		//"spring.jpa.show-sql=true",
		"spring.jpa.hibernate.ddl-auto=none",
		"spring.geode.sample.async-inline-caching.queue.batch-size=2"
	},
	webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@SuppressWarnings("unused")
public class AsyncInlineCachingUsingQueueBatchSizeIntegrationTests extends IntegrationTestsSupport {

	private volatile AsyncEventQueue golfersEventQueue;

	@Autowired
	private GolferService golferService;

	@Resource(name = "Golfers")
	private Region<String, Golfer> golfers;

	private volatile RepositoryAsyncEventListener<Golfer, String> golfersEventQueueListener;

	@Before
	public void assertGolfApplicationState() {

		assertThat(this.golferService).isNotNull();
		assertThat(this.golferService.getAllGolfersFromCache()).isEmpty();
		assertThat(this.golferService.getAllGolfersFromDatabase()).isEmpty();
	}

	@Before
	@SuppressWarnings("unchecked")
	public void assertRegionAndAsyncEventQueueSetup() throws Exception {

		assertThat(this.golfers).isNotNull();
		assertThat(this.golfers.getName()).isEqualTo("Golfers");
		assertThat(this.golfers.getAttributes()).isNotNull();
		assertThat(this.golfers.getAttributes().getDataPolicy()).isEqualTo(DataPolicy.REPLICATE);
		assertThat(this.golfers.getAttributes().getAsyncEventQueueIds()).hasSize(1);

		String asyncEventQueueId = this.golfers.getAttributes().getAsyncEventQueueIds().iterator().next();

		assertThat(asyncEventQueueId).isNotEmpty();
		assertThat(asyncEventQueueId).startsWith("Golfers-AEQ-");

		AsyncEventQueue golfersEventQueue = Optional.of(this.golfers)
			.map(Region::getRegionService)
			.filter(Cache.class::isInstance)
			.map(Cache.class::cast)
			.map(cache -> cache.getAsyncEventQueue(asyncEventQueueId))
			.orElseThrow(() -> newIllegalStateException("AEQ with ID [%s] not found", asyncEventQueueId));

		assertThat(golfersEventQueue).isNotNull();
		assertThat(golfersEventQueue.getBatchSize()).isEqualTo(2);
		assertThat(golfersEventQueue.getBatchTimeInterval()).isEqualTo(Duration.ofMinutes(15).toMillis());
		assertThat(golfersEventQueue.getDispatcherThreads()).isEqualTo(1);
		assertThat(golfersEventQueue.isParallel()).isFalse();

		this.golfersEventQueue = golfersEventQueue;

		AsyncEventListener listener = golfersEventQueue.getAsyncEventListener();

		assertThat(listener).isInstanceOf(RepositoryAsyncEventListener.class);

		RepositoryAsyncEventListener<Golfer, String> repositoryListener =
			(RepositoryAsyncEventListener<Golfer, String>) listener;

		assertThat(ReflectionUtils.<Object>getFieldValue(repositoryListener, "repository"))
			.isInstanceOf(GolferRepository.class);

		this.golfersEventQueueListener = repositoryListener;
	}

	@Test
	public void databaseIsOnlyUpdatedOnCacheRegionQueueBatchSizeLimits() {

		assertThat(this.golfersEventQueue).isNotNull();
		assertThat(this.golfersEventQueueListener).isNotNull();

		Golfer tigerWoods = Golfer.newGolfer("Tiger Woods");

		this.golferService.update(tigerWoods);

		assertThat(this.golferService.getAllGolfersFromCache()).containsExactly(tigerWoods);
		assertThat(this.golferService.getAllGolfersFromDatabase()).isEmpty();

		Golfer arnoldPalmer = Golfer.newGolfer("Arnold Palmer");
		Golfer harryFowler = Golfer.newGolfer("Harry Fowler");
		Golfer roryMcIlroy = Golfer.newGolfer("Rory McIlroy");

		this.golferService.update(roryMcIlroy);
		this.golferService.update(harryFowler);
		this.golferService.update(arnoldPalmer);

		assertThat(this.golferService.getAllGolfersFromCache())
			.containsExactly(arnoldPalmer, harryFowler, roryMcIlroy, tigerWoods);

		assertThat(this.golfers.get(tigerWoods.getName())).isEqualTo(tigerWoods);
		assertThat(this.golfers).hasSize(4);

		// Assert the size of the "Golfers" Region, AEQ only for debugging purposes!
		//assertThat(this.golfersEventQueue.size()).isGreaterThanOrEqualTo(2);

		Awaitility.with().pollInSameThread().pollDelay(Duration.ofSeconds(1L))
			.await().atMost(Duration.ofSeconds(15))
			.until(this.golfersEventQueueListener::hasFired);

		assertThat(this.golferService.getAllGolfersFromDatabase())
			.containsExactly(arnoldPalmer, harryFowler, roryMcIlroy, tigerWoods);
	}

	@PeerCacheApplication
	@EnableAutoConfiguration
	@EntityScan(basePackageClasses = Golfer.class)
	@EnableJpaRepositories(basePackageClasses = GolferRepository.class)
	@Import({ AsyncInlineCachingConfiguration.class, AsyncInlineCachingRegionConfiguration.class })
	static class TestConfiguration {

		@Bean
		GolferService golferService(@Qualifier("golfersTemplate") GemfireTemplate golfersTemplate,
				GolferRepository golferRepository) {

			return new GolferService(golfersTemplate, golferRepository);
		}
	}
}