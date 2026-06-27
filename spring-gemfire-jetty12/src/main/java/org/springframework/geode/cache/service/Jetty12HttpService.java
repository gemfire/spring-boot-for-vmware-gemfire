/*
 * Copyright 2023-2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.cache.service;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.geode.cache.Cache;
import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.internal.HttpService;
import org.apache.geode.distributed.internal.DistributionConfig;
import org.apache.geode.distributed.internal.InternalDistributedSystem;
import org.apache.geode.internal.cache.CacheService;
import org.apache.geode.internal.net.SSLConfig;
import org.apache.geode.internal.net.SSLConfigurationFactory;
import org.apache.geode.internal.net.SSLUtil;
import org.apache.geode.internal.security.SecurableCommunicationChannel;
import org.apache.geode.management.internal.beans.CacheServiceMBeanBase;
import org.eclipse.jetty.server.ConnectionFactory;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SymlinkAllowedResourceAliasChecker;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.ResourceFactory;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.geode.cache.service.classloader.JettyRestrictedParentResourcesClassLoader;
import org.springframework.geode.util.CacheUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * An Apache Geode {@link HttpService} implementation using Eclipse Jetty 12 HTTP server and Servlet container.
 *
 * <p>WAR deployment is transparent with respect to the Servlet API generation: if the WAR's
 * {@code WEB-INF/web.xml} declares the Jakarta EE namespace
 * ({@code https://jakarta.ee/xml/ns/jakartaee}) then an EE10
 * {@link org.eclipse.jetty.ee10.webapp.WebAppContext} is used; otherwise an EE8
 * {@link org.eclipse.jetty.ee8.webapp.WebAppContext} is used, which natively understands
 * {@code javax.servlet.*} WARs without any bytecode migration.
 *
 * @author John Blum
 * @see org.apache.geode.cache.Cache
 * @see org.apache.geode.cache.internal.HttpService
 * @see org.eclipse.jetty.server.Server
 * @see org.eclipse.jetty.ee8.webapp.WebAppContext
 * @see org.eclipse.jetty.ee10.webapp.WebAppContext
 * @since 2.0.0
 */
public class Jetty12HttpService implements HttpService {

	private static final boolean JETTY_WEBAPP_PARENT_LOADER_PRIORITY = false;
	private static final boolean SKIP_SSL_VERIFICATION = false;

	private static final String APACHE_GEODE_ANY_SSL_CIPHERS = "any";
	private static final String APACHE_GEODE_CONFIGURATION_ATTRIBUTE_NAME = "apache.geode.cache.configuration";
	private static final String APACHE_GEODE_JETTY_THREAD_POOL_NAME = "ApacheGeode-EclipseJetty-ThreadPool";
	private static final String JAKARTA_EE_NAMESPACE = "https://jakarta.ee/xml/ns/jakartaee";
	private static final String UNDERSCORE = "_";

	private static <K, V> Map<K, V> nullSafeMap(Map<K, V> map) {
		return map != null ? map : Collections.emptyMap();
	}

	private static String nullSafeString(String value, String defaultValue) {
		return StringUtils.isNotBlank(value) ? value : String.valueOf(defaultValue);
	}

	private static <T> T requireObject(T object, String message, Object... args) {

		if (object == null) {
			throw new IllegalArgumentException(String.format(message, args));
		}

		return object;
	}

	private static <T> String[] toArray(String commaDelimitedString) {
		return StringUtils.isNotBlank(commaDelimitedString)
			? commaDelimitedString.split(",")
			: new String[0];
	}

	private static <T> Supplier<T> toSupplier(Supplier<T> lambda) {
		return lambda;
	}

	private final List<Object> webApplications = new CopyOnWriteArrayList<>();

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private volatile Server server;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Class<? extends CacheService> getInterface() {
		return HttpService.class;
	}

	/**
	 * Return a reference to the configured SLF4J {@link Logger}.
	 *
	 * @return a reference to the configured SLF4J {@link Logger}.
	 * @see org.slf4j.Logger
	 */
	protected Logger getLogger() {
		return this.logger;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CacheServiceMBeanBase getMBean() {
		return null;
	}

	/**
	 * Gets a reference to the configured and initialized Eclipse Jetty HTTP server and Servlet container.
	 *
	 * @return a reference to the configured and initialized Eclipse Jetty HTTP server and Servlet container;
	 * may be {@literal null} if the {@link Server} has not yet been initialized.
	 * @see org.eclipse.jetty.server.Server
	 * @see #getOptionalServer()
	 * @see #init(Cache)
	 */
	protected Server getServer() {
		return this.server;
	}

	/**
	 * Gets an {@link Optional} reference to the configured and initialized Eclipse Jetty HTTP server
	 * and Servlet container.
	 *
	 * @return an {@link Optional} reference to the configured and initialized Eclipse Jetty HTTP server
	 * and Servlet container.
	 * @see org.eclipse.jetty.server.Server
	 * @see java.util.Optional
	 * @see #getServer()
	 */
	protected Optional<Server> getOptionalServer() {
		return Optional.ofNullable(getServer());
	}

	/**
	 * Gets the list of deployed web application {@link Handler} instances.
	 *
	 * <p>Each entry is either an {@link org.eclipse.jetty.ee8.webapp.WebAppContext} (for
	 * {@code javax.servlet}-based WARs) or an {@link org.eclipse.jetty.ee10.webapp.WebAppContext}
	 * (for {@code jakarta.servlet}-based WARs), chosen transparently based on the WAR's
	 * {@code WEB-INF/web.xml} namespace.
	 *
	 * @return an unmodifiable view of the deployed web application handlers.
	 * @see org.eclipse.jetty.server.Handler
	 * @see java.util.List
	 */
	protected List<Object> getWebApplications() {
		return Collections.unmodifiableList(this.webApplications);
	}

	/**
	 * Detects whether the WAR at the given path is a Jakarta EE (EE10) WAR by inspecting
	 * the XML namespace declared in {@code WEB-INF/web.xml}.
	 *
	 * <p>Returns {@code true} only when the web.xml declares
	 * {@value #JAKARTA_EE_NAMESPACE} as a quote-delimited XML namespace value (i.e. the
	 * root element's {@code xmlns} default namespace). A namespace that merely appears
	 * inside an {@code xsi:schemaLocation} pair — where it is followed by a whitespace and
	 * the schema URL rather than a closing quote — does not count, since that does not
	 * determine the servlet API generation. Returns {@code false} for Java EE /
	 * javax.servlet WARs, WARs with no {@code web.xml}, and any WAR that cannot be opened.
	 *
	 * @param warFilePath path to the WAR file to inspect; must not be {@literal null}.
	 * @return {@code true} if the WAR targets the Jakarta EE servlet API; {@code false} otherwise.
	 */
	boolean isJakartaEEWar(Path warFilePath) {
		try (FileSystem zip = FileSystems.newFileSystem(warFilePath)) {
			Path webXml = zip.getPath("WEB-INF/web.xml");
			if (!Files.exists(webXml)) {
				return false;
			}
			String content = Files.readString(webXml);
			// Match the namespace only when quote-delimited (a default-namespace declaration),
			// not when it merely occurs as the first token of an xsi:schemaLocation pair.
			return content.contains("\"" + JAKARTA_EE_NAMESPACE + "\"")
				|| content.contains("'" + JAKARTA_EE_NAMESPACE + "'");
		}
		// IOException covers unreadable/corrupt WARs; FileSystemAlreadyExistsException (a
		// RuntimeException, thrown if the WAR's zip FileSystem is already open) and any other
		// runtime failure must also fall back to the safe EE8 default rather than propagate.
		catch (IOException | RuntimeException e) {
			return false;
		}
	}

	/**
	 * Initializes the internal, embedded Apache Geode {@link HttpService} by creating an instance of
	 * the Eclipse Jetty 12 HTTP server and Servlet container.
	 *
	 * @param cache reference to the {@literal peer} {@link Cache} instance
	 * in which this embedded {@link HttpService} will be running.
	 * @return a boolean value indicating whether the Eclipse Jetty 12 based {@link HttpService} constructed,
	 * configured and initialized.
	 * @see org.apache.geode.cache.Cache
	 */
	@Override
	public boolean init(Cache cache) {

		return Optional.ofNullable(cache)
			.filter(CacheUtils::isPeerCache)
			.map(this::resolveDistributedSystem)
			.map(InternalDistributedSystem::getConfig)
			.filter(this::isHttpServiceEnabled)
			.map(this::initializeHttpServiceServer)
			.isPresent();
	}

	private InternalDistributedSystem resolveDistributedSystem(Cache cache) {

		return Optional.ofNullable(cache)
			.map(GemFireCache::getDistributedSystem)
			.filter(InternalDistributedSystem.class::isInstance)
			.map(InternalDistributedSystem.class::cast)
			.orElse(null);
	}

	private boolean isHttpServiceEnabled(DistributionConfig configuration) {

		int httpServicePort = configuration.getHttpServicePort();

		boolean httpServiceEnabled = httpServicePort > -1;

		if (!httpServiceEnabled) {
			logInfo("Apache Geode's embedded HttpService is disabled;"
				+ " {} is set to [{}]", DistributionConfig.HTTP_SERVICE_PORT_NAME, httpServicePort);
		}

		return httpServiceEnabled;
	}

	private Server initializeHttpServiceServer(DistributionConfig configuration) {

		Server server = new Server(newThreadPool(configuration));

		server.addConnector(newConnector(configuration, server));
		server.setAttribute(APACHE_GEODE_CONFIGURATION_ATTRIBUTE_NAME, configuration);
		server.setHandler(new ContextHandlerCollection(true));

		logInfo("Initializing Apache Geode's embedded HTTP service with the Jetty {} Server...",
			toSupplier(Server::getVersion));

		this.server = server;

		return server;
	}

	@SuppressWarnings("unused")
	private ThreadPool newThreadPool(DistributionConfig configuration) {

		QueuedThreadPool threadPool = new QueuedThreadPool();

		threadPool.setName(APACHE_GEODE_JETTY_THREAD_POOL_NAME);

		return threadPool;
	}

	private Connector newConnector(DistributionConfig configuration, Server server) {

		String httpServiceBindAddress = configuration.getHttpServiceBindAddress();

		int httpServicePort = configuration.getHttpServicePort();

		logInfo("Apache Geode's embedded HTTP service will run on host [{}] and listen on port [{}]",
			httpServiceBindAddress, httpServicePort);

		ConnectionFactory[] connectionFactories =
			newConnectionFactories(configuration).toArray(new ConnectionFactory[0]);

		ServerConnector connector = new ServerConnector(server, connectionFactories);

		connector.setHost(httpServiceBindAddress);
		connector.setPort(httpServicePort);

		return connector;
	}

	private List<ConnectionFactory> newConnectionFactories(DistributionConfig configuration) {

		List<ConnectionFactory> connectionFactories = new ArrayList<>();

		HttpConnectionFactory httpConnectionFactory = newHttpConnectionFactory(configuration);

		newSslConnectionFactory(configuration, httpConnectionFactory)
			.ifPresent(connectionFactories::add);

		connectionFactories.add(httpConnectionFactory);

		return connectionFactories;
	}

	private HttpConnectionFactory newHttpConnectionFactory(DistributionConfig configuration) {

		HttpConfiguration httpConfiguration = new HttpConfiguration();

		httpConfiguration.setSecurePort(configuration.getHttpServicePort());

		return new HttpConnectionFactory(httpConfiguration);
	}

	private Optional<SslConnectionFactory> newSslConnectionFactory(DistributionConfig configuration,
			HttpConnectionFactory httpConnectionFactory) {

		SSLConfig sslConfiguration =
			SSLConfigurationFactory.getSSLConfigForComponent(configuration, SecurableCommunicationChannel.WEB);

		if (sslConfiguration.isEnabled()) {

			SslContextFactory.Server serverSslContextFactory = new SslContextFactory.Server();

			Optional.ofNullable(sslConfiguration.getAlias())
				.filter(StringUtils::isNotBlank)
				.ifPresent(serverSslContextFactory::setCertAlias);

			Optional.ofNullable(sslConfiguration.getCiphers())
				.filter(this::isSslCiphersConfigured)
				.ifPresent(ciphers -> {
					serverSslContextFactory.setExcludeCipherSuites();
					serverSslContextFactory.setIncludeCipherSuites(toArray(ciphers));
				});

			serverSslContextFactory.setNeedClientAuth(sslConfiguration.isRequireAuth());
			serverSslContextFactory.setSslContext(SSLUtil.createAndConfigureSSLContext(sslConfiguration,
				SKIP_SSL_VERIFICATION));

			httpConnectionFactory.getHttpConfiguration().addCustomizer(new SecureRequestCustomizer());

			logDebug("SSL configuration [{}] for protocol [{}]",
				toSupplier(serverSslContextFactory::dump), toSupplier(httpConnectionFactory::getProtocol));

			return Optional.of(new SslConnectionFactory(serverSslContextFactory, httpConnectionFactory.getProtocol()));
		}

		return Optional.empty();
	}

	private boolean isSslCiphersConfigured(String sslCiphers) {
		return StringUtils.isNotBlank(sslCiphers)
			&& !APACHE_GEODE_ANY_SSL_CIPHERS.equalsIgnoreCase(sslCiphers.trim());
	}

	/**
	 * Adds a Web application to Apache Geode's embedded HTTP service.
	 *
	 * <p>The Servlet API generation (EE8 / {@code javax.servlet} vs EE10 / {@code jakarta.servlet})
	 * is detected automatically from the WAR's {@code WEB-INF/web.xml} namespace — no explicit
	 * configuration is required.
	 *
	 * @param contextPath {@link String} containing the Web application context path.
	 * @param warFilePath {@link Path} to the Java Web Application Archive (WAR) file.
	 * @param attributeNameValuePairs {@link Map} of {@link javax.servlet.ServletContext} or
	 * {@link jakarta.servlet.ServletContext} attributes to set on the deployed context.
	 * @see org.eclipse.jetty.server.Handler
	 * @see org.eclipse.jetty.server.Server
	 * @see #getOptionalServer()
	 */
	@Override
	public void addWebApplication(String contextPath, Path warFilePath, Map<String, Object> attributeNameValuePairs) {

		getOptionalServer().map(server -> {

			logInfo("Adding Web application from path [{}] using context [{}]"
				+ " to Apache Geode's embedded HTTP service", warFilePath, contextPath);

			logInfo("Resolved WAR file path [{}]", warFilePath);

			Object webApp;
			if (isJakartaEEWar(warFilePath)) {
				org.eclipse.jetty.ee10.webapp.WebAppContext ee10 =
					buildEe10WebApp(server, warFilePath, contextPath, attributeNameValuePairs);
				((Handler.Collection) server.getHandler()).addHandler(ee10);
				webApp = ee10;
			}
			else {
				org.eclipse.jetty.ee8.webapp.WebAppContext ee8 =
					buildEe8WebApp(server, warFilePath, contextPath, attributeNameValuePairs);
				((Handler.Collection) server.getHandler()).addHandler(ee8);
				webApp = ee8;
			}

			startWebApplication(server, webApp, contextPath);

			return true;
		})
		.orElseGet(() -> {

			logInfo("Unable to add Web application from path [{}] using context [{}]"
				+ " since the Apache Geode embedded HTTP service was not enabled", warFilePath, contextPath);

			return false;
		});
	}

	// -------------------------------------------------------------------------
	// EE8 (javax.servlet) WAR deployment
	// -------------------------------------------------------------------------

	private org.eclipse.jetty.ee8.webapp.WebAppContext buildEe8WebApp(Server server, Path warFilePath, String contextPath,
			Map<String, Object> extraAttributes) {

		Resource webApp = openWarResource(warFilePath, contextPath);

		org.eclipse.jetty.ee8.webapp.WebAppContext ctx =
			new org.eclipse.jetty.ee8.webapp.WebAppContext(webApp, contextPath);

		ctx.addAliasCheck(new SymlinkAllowedResourceAliasChecker(ctx.getCoreContextHandler()));
		ctx.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "false");
		ctx.setParentLoaderPriority(JETTY_WEBAPP_PARENT_LOADER_PRIORITY);
		ctx.setServer(server);

		try {
			ctx.setClassLoader(new JettyRestrictedParentResourcesClassLoader(
				new URL[]{warFilePath.toUri().toURL()}, this.getClass().getClassLoader()));
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}

		configureEe8Classpath(ctx);
		configureTempDirectory(ctx, server, contextPath);

		// Disable javax WebSocket scanning to avoid conflicts with GemFire management WARs
		ctx.setAttribute("org.eclipse.jetty.websocket.javax", false);

		nullSafeMap(extraAttributes).forEach(ctx::setAttribute);

		return ctx;
	}

	private void configureEe8Classpath(org.eclipse.jetty.ee8.webapp.WebAppContext ctx) {

		org.eclipse.jetty.ee8.webapp.ClassMatcher classMatcher = ctx.getServerClassMatcher();
		classMatcher.include("com.fasterxml.jackson.");
		classMatcher.exclude("com.fasterxml.jackson.annotation.");

		File workingDirectory = new File(System.getProperty("user.dir")).getAbsoluteFile();

		try (ResourceFactory.Closeable resourceFactory = ResourceFactory.closeable()) {
			ctx.setExtraClasspath(Collections.singletonList(
				resourceFactory.newResource(workingDirectory.toPath())));
		}
	}

	// -------------------------------------------------------------------------
	// EE10 (jakarta.servlet) WAR deployment
	// -------------------------------------------------------------------------

	private org.eclipse.jetty.ee10.webapp.WebAppContext buildEe10WebApp(Server server, Path warFilePath, String contextPath,
			Map<String, Object> extraAttributes) {

		Resource webApp = openWarResource(warFilePath, contextPath);

		org.eclipse.jetty.ee10.webapp.WebAppContext ctx =
			new org.eclipse.jetty.ee10.webapp.WebAppContext(webApp, contextPath);

		ctx.addAliasCheck(new SymlinkAllowedResourceAliasChecker(ctx));
		ctx.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "false");
		ctx.setParentLoaderPriority(JETTY_WEBAPP_PARENT_LOADER_PRIORITY);
		ctx.setServer(server);

		try {
			ctx.setClassLoader(new JettyRestrictedParentResourcesClassLoader(
				new URL[]{warFilePath.toUri().toURL()}, this.getClass().getClassLoader()));
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}

		configureEe10Classpath(ctx);
		configureTempDirectory(ctx, server, contextPath);

		// Disable jakarta WebSocket scanning to avoid conflicts with GemFire management WARs
		ctx.setAttribute("org.eclipse.jetty.websocket.jakarta", false);

		nullSafeMap(extraAttributes).forEach(ctx::setAttribute);

		return ctx;
	}

	private void configureEe10Classpath(org.eclipse.jetty.ee10.webapp.WebAppContext ctx) {

		org.eclipse.jetty.ee10.webapp.ClassMatcher classMatcher = ctx.getServerClassMatcher();
		classMatcher.include("com.fasterxml.jackson.");
		classMatcher.exclude("com.fasterxml.jackson.annotation.");

		File workingDirectory = new File(System.getProperty("user.dir")).getAbsoluteFile();

		try (ResourceFactory.Closeable resourceFactory = ResourceFactory.closeable()) {
			ctx.setExtraClasspath(Collections.singletonList(
				resourceFactory.newResource(workingDirectory.toPath())));
		}
	}

	// -------------------------------------------------------------------------
	// Shared helpers
	// -------------------------------------------------------------------------

	private Resource openWarResource(Path warFilePath, String contextPath) {
		try (ResourceFactory.Closeable resourceFactory = ResourceFactory.closeable()) {
			return resourceFactory.newResource(requireObject(warFilePath,
				String.format("WAR file path of the Web application [%s] to add must not be null", contextPath)));
		}
	}

	/**
	 * Configures a temp directory on either an EE8 or EE10 {@link Handler} that is also a
	 * Jetty context.  Both {@code org.eclipse.jetty.ee8.webapp.WebAppContext} and
	 * {@code org.eclipse.jetty.ee10.webapp.WebAppContext} expose a {@code setTempDirectory(File)}
	 * method; this helper dispatches to the correct one via pattern matching.
	 */
	private void configureTempDirectory(Object handler, Server server, String contextPath) {

		DistributionConfig configuration = requireObject(
			(DistributionConfig) server.getAttribute(APACHE_GEODE_CONFIGURATION_ATTRIBUTE_NAME),
			"DistributionConfig was not stored in the Server Attributes");

		// Preserve the original null/blank fallback so a missing context path cannot NPE here.
		String resolvedContextPath = nullSafeString(contextPath, "defaultContext");

		String safeContextPath = (resolvedContextPath.startsWith(File.separator)
			? resolvedContextPath.substring(1) : resolvedContextPath)
			.replace(File.separator, UNDERSCORE);

		String hostPort = nullSafeString(configuration.getHttpServiceBindAddress(), "0.0.0.0")
			.concat(UNDERSCORE)
			.concat(String.valueOf(configuration.getHttpServicePort()));

		String uuid = UUID.randomUUID().toString().substring(0, 8);

		String[] tempPathElements = {"temp", System.getProperty("user.name"), "geode",
			"services", "http", hostPort, safeContextPath, uuid};

		Path tempDirectoryPath = FileSystems.getDefault()
			.getPath(System.getProperty("user.dir"), tempPathElements);

		File tempDirectory = tempDirectoryPath.toFile();
		tempDirectory.mkdirs();
		tempDirectory.deleteOnExit();

		if (handler instanceof org.eclipse.jetty.ee8.webapp.WebAppContext ee8) {
			ee8.setTempDirectory(tempDirectory);
		}
		else if (handler instanceof org.eclipse.jetty.ee10.webapp.WebAppContext ee10) {
			ee10.setTempDirectory(tempDirectory);
		}
	}

	private Object startWebApplication(Server server, Object webApp, String contextPath) {

		logInfo("Starting Web application in context [{}]...", contextPath);

		if (!server.isStarted()) {
			SafeServerWrapper.from(server).safeStart();
		}
		else {
			SafeWebApplicationWrapper.from(webApp, contextPath).safeStart();
		}

		this.webApplications.add(webApp);

		return webApp;
	}

	/**
	 * Stops Apache Geode's internal, embedded {@link HttpService}.
	 */
	@Override
	public void close() {

		logInfo("Closing Apache Geode's embedded HTTP service...");

		getWebApplications().stream()
			.map(SafeWebApplicationWrapper::from)
			.forEach(SafeWebApplicationWrapper::safeStop);

		getOptionalServer()
			.map(SafeServerWrapper::from)
			.ifPresent(SafeServerWrapper::safeStopAndDestroy);

		File tempDirectory = new File(System.getProperty("user.dir"), "temp");

		try {
			FileUtils.deleteDirectory(tempDirectory);
		}
		catch (IOException cause) {
			logWarn(cause, "Failed to delete the temp directory [{}]", tempDirectory);
		}
	}

	private void log(Predicate<Logger> loggerPredicate, Consumer<Logger> loggerConsumer) {

		Logger logger = getLogger();

		if (loggerPredicate.test(logger)) {
			loggerConsumer.accept(logger);
		}
	}

	private void logDebug(String message, Object... arguments) {
		log(Logger::isDebugEnabled, it -> it.debug(message, resolveArguments(arguments)));
	}

	private void logInfo(String message, Object... arguments) {
		log(Logger::isInfoEnabled, it -> it.info(message, resolveArguments(arguments)));
	}

	private void logWarn(Throwable cause, String message, Object... arguments) {
		log(Logger::isWarnEnabled, it ->
			it.warn(MessageFormatter.format(message, resolveArguments(arguments)).getMessage(), cause));
	}

	private Object[] resolveArguments(Object... arguments) {

		List<Object> resolvedArguments = new ArrayList<>(arguments.length);

		for (Object argument : arguments) {
			Object resolvedArgument = (argument instanceof Supplier<?> supplier) ? supplier.get() : argument;
			resolvedArguments.add(resolvedArgument);
		}

		return resolvedArguments.toArray();
	}

	@SuppressWarnings("unused")
	protected static class SafeServerWrapper extends Server {

		public static SafeServerWrapper from(Server server) {
			return new SafeServerWrapper(server);
		}

		private final Logger logger = LoggerFactory.getLogger(Jetty12HttpService.class);

		private final Server server;

		private SafeServerWrapper(Server server) {
			this.server = requireObject(server, "Server must not be null");
		}

		protected Logger getLogger() {
			return this.logger;
		}

		public void safeStart() {

			Server serverReference = this.server;

			try {
				serverReference.start();
			}
			catch (Exception cause) {
				throw new ServerException(String.format("Failed to start HTTP server [%s]",
					serverReference), cause);
			}
		}

		public void safeStop() {

			Server serverReference = this.server;

			try {
				serverReference.stop();
			}
			catch (Exception cause) {
				getLogger().warn("Failed to stop HTTP server [{}}]", serverReference);
				getLogger().debug("", cause);
			}
		}

		public void safeDestroy() {

			Server serverReference = this.server;

			try {
				serverReference.destroy();
			}
			catch (Throwable cause) {
				getLogger().warn("Failed to release system resources used by HTTP server [{}]", serverReference);
				getLogger().debug("", cause);
			}
		}

		public void safeStopAndDestroy() {
			safeStop();
			safeDestroy();
		}
	}

	protected static class SafeWebApplicationWrapper {

		private final Logger logger = LoggerFactory.getLogger(Jetty12HttpService.class);

		private final Object webApp;
		private final String contextPath;

		private SafeWebApplicationWrapper(Object webApp, String contextPath) {
			this.webApp = requireObject(webApp, "WebApp must not be null");
			this.contextPath = contextPath;
		}

		/**
		 * Creates a wrapper with an explicit context path (used during deployment, where the
		 * path is already known without inspecting the handler).
		 */
		public static SafeWebApplicationWrapper from(Object webApp, String contextPath) {
			return new SafeWebApplicationWrapper(webApp, contextPath);
		}

		/**
		 * Creates a wrapper extracting the context path from the handler type (used during
		 * shutdown, where only the stored web application object is available).
		 */
		public static SafeWebApplicationWrapper from(Object webApp) {
			String path;
			if (webApp instanceof org.eclipse.jetty.ee8.webapp.WebAppContext ee8) {
				path = ee8.getContextPath();
			}
			else if (webApp instanceof org.eclipse.jetty.ee10.webapp.WebAppContext ee10) {
				path = ee10.getContextPath();
			}
			else {
				path = "unknown";
			}
			return new SafeWebApplicationWrapper(webApp, path);
		}

		public void safeStart() {

			try {
				((LifeCycle) this.webApp).start();
				this.logger.info("Started Web application in context [{}]", this.contextPath);
			}
			catch (Exception cause) {
				this.logger.error("Failed to start Web application in context [{}]", this.contextPath, cause);
				throw new WebApplicationException(
					String.format("Failed to start Web application in context [%s]", this.contextPath), cause);
			}
		}

		public void safeStop() {

			try {
				((LifeCycle) this.webApp).stop();
			}
			catch (Exception cause) {
				this.logger.warn("Failed to stop Web application in context [{}]", this.contextPath);
				this.logger.debug("", cause);
			}
		}
	}

	@SuppressWarnings("unused")
	protected static class JettyException extends RuntimeException {

		protected JettyException() { }

		protected JettyException(String message) {
			super(message);
		}

		protected JettyException(Throwable cause) {
			super(cause);
		}

		protected JettyException(String message, Throwable cause) {
			super(message, cause);
		}
	}

	@SuppressWarnings("unused")
	protected static class ServerException extends JettyException {

		public ServerException() { }

		public ServerException(String message) {
			super(message);
		}

		public ServerException(Throwable cause) {
			super(cause);
		}

		public ServerException(String message, Throwable cause) {
			super(message, cause);
		}
	}

	@SuppressWarnings("unused")
	protected static class WebApplicationException extends JettyException {

		protected WebApplicationException() { }

		protected WebApplicationException(String message) {
			super(message);
		}

		protected WebApplicationException(Throwable cause) {
			super(cause);
		}

		protected WebApplicationException(String message, Throwable cause) {
			super(message, cause);
		}
	}
}
