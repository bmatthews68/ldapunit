/*
 * Copyright 2013 Brian Thomas Matthews
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.btmatthews.ldapunit;

import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPConnectionOptions;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.SearchResultEntry;

/**
 * Utility that maintains a connection to the LDAP directory server and provides assert and verify methods to
 * test the LDAP directory contents.
 *
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 1.0.0
 */
public class DirectoryTester {

    /**
     * The default for the maximum connection attempts.
     *
     * @since 1.0.1
     */
    private final static int DEFAULT_RETRIES = 3;
    /**
     * The default for the connection timeout.
     *
     * @since 1.0.1
     */
    private final static int DEFAULT_TIMEOUT = 5000;
    /**
     * The connection to the LDAP directory server.
     */
    private final LDAPConnection connection = new LDAPConnection();

    /**
     * Initialise the LDAP directory tester using the default hostname of {@code localhost} and port number of
     * {@code 10389}.
     *
     * @since 1.0.1
     */
    public DirectoryTester() {
        this("localhost", 10389);
    }

    /**
     * Initialise the LDAP directory tester by connecting to the LDAP directory server using the {@code hostname} and
     * {@code port}.
     *
     * @param hostname The host name of the directory server.
     * @param port     The TCP port number of the directory server.
     * @throws DirectoryTesterException If there was a problem connecting to the LDAP directory server.
     */
    public DirectoryTester(final String hostname, final int port) {
        this(hostname, port, DEFAULT_RETRIES, DEFAULT_TIMEOUT);
    }

    /**
     * Initialise the LDAP directory tester by connecting to the LDAP directory server using the {@code hostname} and
     * {@code port}.The connection attempt is retried a maximum of {@code retries} times with a timeout of
     * {@code timeout} for each attempt.
     *
     * @param hostname The host name of the directory server.
     * @param port     The TCP port number of the directory server.
     * @param retries  The maximum number of connection attempts.
     * @param timeout  The timeout for each connection attempt.
     * @throws DirectoryTesterException If there was a problem connecting to the LDAP directory server.
     * @since 1.0.1
     */
    public DirectoryTester(final String hostname,
                           final int port,
                           final int retries,
                           final int timeout) {
        final LDAPConnectionOptions options = new LDAPConnectionOptions();
        options.setConnectTimeoutMillis(timeout);
        int attempt = 0;
        for (; ; ) {
            final long startTime = System.currentTimeMillis();
            try {
                connection.connect(hostname, port, timeout);
                break;
            } catch (final LDAPException e) {
                if (attempt++ >= retries) {
                    throw new DirectoryTesterException("Could not connect to LDAP directory server", e);
                } else {
                    long timeDifference = System.currentTimeMillis() - startTime;
                    if (timeDifference < timeout) {
                        try {
                            Thread.sleep(timeout - timeDifference);
                        } catch (final InterruptedException i) {
                            throw new DirectoryTesterException("Could not connect to LDAP directory server", e);
                        }
                    }
                }
            }
        }
    }

    /**
     * Initialise the LDAP directory tester by connecting to the LDAP directory server using the {@code hostname} and
     * {@code port} and bind to it using the {@code bindDN} and {@code password}.
     *
     * @param hostname The host name of the directory server.
     * @param port     The TCP port number of the directory server.
     * @param bindDN   The DN used to bind to the LDAP directory server.
     * @param password The password used to bind to the LDAP directory server.
     * @throws DirectoryTesterException If there was a problem connecting to the LDAP directory server.
     */
    public DirectoryTester(final String hostname,
                           final int port,
                           final String bindDN,
                           final String password) {
        this(hostname, port, bindDN, password, DEFAULT_RETRIES, DEFAULT_TIMEOUT);
    }

    /**
     * Initialise the LDAP directory tester by connecting to the LDAP directory server using the {@code hostname} and
     * {@code port} and bind to it using the {@code bindDN} and {@code password}. The connection attempt is retried
     * a maximum of {@code retries} times with a timeout of {@code timeout} for each attempt.
     *
     * @param hostname The host name of the directory server.
     * @param port     The TCP port number of the directory server.
     * @param bindDN   The DN used to bind to the LDAP directory server.
     * @param password The password used to bind to the LDAP directory server.
     * @param retries  The maximum number of connection attempts.
     * @param timeout  The timeout for each connection attempt.
     * @throws DirectoryTesterException If there was a problem connecting to the LDAP directory server.
     * @since 1.0.1
     */
    public DirectoryTester(final String hostname,
                           final int port,
                           final String bindDN,
                           final String password,
                           final int retries,
                           final int timeout) {
        this(hostname, port, retries, timeout);
        try {
            connection.bind(bindDN, password);
        } catch (final LDAPException e) {
            throw new DirectoryTesterException("Could not bind to LDAP directory server", e);
        }
    }

    /**
     * Verify that an entry identified by {@code dn} exists.
     *
     * @param dn The distinguished name.
     * @return {@code true} if an entry identified by {@code dn} exists. Otherwise, {@code false} is returned.
     */
    public boolean verifyDNExists(final String dn) {
        try {
            final SearchResultEntry entry = connection.getEntry(dn);
            return entry != null;
        } catch (final LDAPException e) {
            throw new DirectoryTesterException("Error communicating with LDAP directory server", e);
        }
    }

    /**
     * Assert that an entry identified by {@code dn} exists.
     *
     * @param dn The distinguished name.
     */
    public void assertDNExists(final String dn) {
        if (!verifyDNExists(dn)) {
            final StringBuilder message = new StringBuilder("DN does not exist: ");
            message.append(dn);
            throw new AssertionError(message);
        }
    }

    /**
     * Disconnect from the LDAP directory server.
     */
    public void disconnect() {
        connection.close();
    }
}
