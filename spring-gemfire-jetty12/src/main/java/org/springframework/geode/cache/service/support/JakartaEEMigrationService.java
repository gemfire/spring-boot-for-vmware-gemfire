/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.geode.cache.service.support;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Predicate;

import org.apache.tomcat.jakartaee.EESpecProfile;
import org.apache.tomcat.jakartaee.EESpecProfiles;
import org.apache.tomcat.jakartaee.Migration;

/**
 * Service class used to migrate a Java EE WAR (archive) to a Jakarta EE WAR (archive) using the Apache Tomcat
 * Jakarta EE Migration Tool.
 *
 * @author John Blum
 * @see java.io.File
 * @see java.nio.file.Files
 * @see java.nio.file.Path
 * @see org.apache.tomcat.jakartaee.EESpecProfile
 * @see org.apache.tomcat.jakartaee.Migration
 * @since 2.0.0
 */
@SuppressWarnings("unused")
public class JakartaEEMigrationService {

	public static final JakartaEEMigrationService INSTANCE = new JakartaEEMigrationService();

	private static final String MIGRATED_FILENAME_PREFIX = "jakartaee-";
	private static final String MIGRATED_FILENAME_PATTERN = MIGRATED_FILENAME_PREFIX.concat("%s");

	private static <T> T requireObject(T object, Predicate<T> predicate, String message, Object... arguments) {

		if (!predicate.test(object)) {
			throw new IllegalArgumentException(String.format(message, arguments));
		}

		return object;
	}

	public Path migrate(Path warFile) {

		Path resolvedWarFile = requireObject(warFile, (Path path) -> Objects.nonNull(path) && Files.exists(path),
			"The Path to the WAR file [%s] must not be null and exist", warFile);

		String warFileName = resolvedWarFile.getFileName().toString();
		String migratedWarFileName = String.format(MIGRATED_FILENAME_PATTERN, warFileName);

		File migratedWarFile = new File(resolvedWarFile.getParent().toFile(), migratedWarFileName);

		if (!migratedWarFile.isFile()) {
			try {
				migratedWarFile.createNewFile();

				Migration migration = new Migration();

				migration.setSource(resolvedWarFile.toFile());
				migration.setDestination(migratedWarFile);
				migration.setEESpecProfile(EESpecProfiles.EE);
				migration.execute();
			}
			catch (IOException cause) {
				throw new JavaEEJakartaEEMigrationException(
					String.format("Failed to migrate Java EE WAR file [%s] to Jakarta EE", warFile), cause);
			}
		}

		return migratedWarFile.toPath();
	}

	protected static class JavaEEJakartaEEMigrationException extends RuntimeException {

		public JavaEEJakartaEEMigrationException() { }

		public JavaEEJakartaEEMigrationException(String message) {
			super(message);
		}

		public JavaEEJakartaEEMigrationException(Throwable cause) {
			super(cause);
		}

		public JavaEEJakartaEEMigrationException(String message, Throwable cause) {
			super(message, cause);
		}
	}
}
