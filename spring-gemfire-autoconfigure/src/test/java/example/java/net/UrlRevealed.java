/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package example.java.net;

import java.net.URL;

import org.springframework.core.io.ClassPathResource;

/**
 * The {@link UrlRevealed} class is a disclosure of Java's {@link URL} class.
 *
 * @author John Blum
 * @see java.net.URL
 * @since 1.0.0
 */
public class UrlRevealed {

	public static void main(String[] args) throws Exception {

		URL url = new URL("jar:file:///www.foo.com/bar/jar.jar!/baz/entry.txt");

		System.out.printf("URL [%s] {%n \tfile [%s],%n \tpath [%s],%n \tport [%s],%n \tprotocol [%s],%n \tquery [%s]%n}%n%n",
			url, url.getFile(), url.getPath(), url.getPort(), url.getProtocol(), url.getQuery());

		System.out.printf("URI [%s]%n", new ClassPathResource("trusted.keystore").getURL().toURI());
	}
}
