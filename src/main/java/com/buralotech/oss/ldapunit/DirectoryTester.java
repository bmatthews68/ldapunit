/*
 * Copyright 2013-2025 Brian Thomas Matthews
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

package com.buralotech.oss.ldapunit;

import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPConnectionOptions;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.SearchResultEntry;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Utility that maintains a connection to the LDAP directory server and provides assert and verify methods to
 * test the LDAP directory contents.
 *
 * @author <a href="mailto:bmatthews68@gmail.com">Brian Matthews</a>
 * @since 1.0.0
 */
public final class DirectoryTester implements AutoCloseable {

    /**
     * The default for the maximum connection attempts.
     *
     * @since 1.0.1
     */
    private static final int DEFAULT_RETRIES = 3;
    /**
     * The default for the connection timeout.
     *
     * @since 1.0.1
     */
    private static final int DEFAULT_TIMEOUT = 5000;
    /**
     * The connection to the LDAP directory server.
     */
    private final LDAPConnection connection;

    /**
     * Initialise the LDAP directory tester using an existing LDAP connection.
     *
     * @param connection The LDAP connection.
     */
    public DirectoryTester(final LDAPConnection connection) {
        this.connection = connection;
    }

    /**
     * Initialise the LDAP directory tester using the default hostname of {@code localhost} and port number of
     * {@link DirectoryServerConfiguration#DEFAULT_PORT}.
     *
     * @since 1.0.1
     */
    public DirectoryTester() {
        this("localhost", DirectoryServerConfiguration.DEFAULT_PORT);
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
        this(new LDAPConnection());
        final LDAPConnectionOptions options = new LDAPConnectionOptions();
        options.setConnectTimeoutMillis(timeout);
        int attempt = 0;
        while (true) {
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
     * Verify that the entry identified by {@code dn} is of type {@code objectclass}.
     *
     * @param dn          The distinguished name.
     * @param objectclass The type name.
     * @return {@code true} if an entry identified by {@code dn} exists and has attribute named {@code objectclass}.
     * Otherwise, {@code false} is returned.
     */
    public boolean verifyDNIsA(final String dn,
                               final String objectclass) {
        try {
            final SearchResultEntry entry = connection.getEntry(dn, "objectclass");
            return entry != null
                    && entry.hasAttribute("objectclass")
                    && arrayContains(entry.getAttributeValues("objectclass"), objectclass);
        } catch (final LDAPException e) {
            throw new DirectoryTesterException("Error communicating with LDAP directory server", e);
        }
    }

    /**
     * Verify that the entry identified by {@code dn} has an attribute named {@code attributeName}.
     *
     * @param dn            The distinguished name.
     * @param attributeName The attribute name.
     * @return {@code true} if an entry identified by {@code dn} exists and has an attributed named
     * {@code attributeName}. Otherwise, {@code false} is returned.
     */
    public boolean verifyDNHasAttribute(final String dn,
                                        final String attributeName) {
        try {
            final SearchResultEntry entry = connection.getEntry(dn, attributeName);
            return entry != null && entry.hasAttribute(attributeName);
        } catch (final LDAPException e) {
            throw new DirectoryTesterException("Error communicating with LDAP directory server", e);
        }
    }

    /**
     * Verify that the entry identified by {@code dn} has an attribute named {@code attributeName} with
     * the attribute value(s) {@code attributeName}.
     *
     * @param dn             The distinguished name.
     * @param attributeName  The attribute name.
     * @param attributeValue The attribute value(s).
     * @return {@code true} if an antry identified by {@code dn} exists with an an attribute named {@code attributeName}
     * that has value(s) {@code attributeValue}. Otherwise, {@code false} is returned.
     */
    public boolean verifyDNHasAttributeValue(final String dn,
                                             final String attributeName,
                                             final String... attributeValue) {
        try {
            final SearchResultEntry entry = connection.getEntry(dn, attributeName);
            if (entry != null && entry.hasAttribute(attributeName)) {
                final Set<String> expectedValues = new HashSet<>(Arrays.asList(attributeValue));
                final Set<String> actualValues = new HashSet<>(Arrays.asList(entry.getAttributeValues(attributeName)));
                if (actualValues.containsAll(expectedValues)) {
                    actualValues.removeAll(expectedValues);
                    if (actualValues.size() == 0) {
                        return true;
                    }
                }
            }
        } catch (final LDAPException e) {
            throw new DirectoryTesterException("Error communicating with LDAP directory server", e);
        }
        return false;
    }

    /**
     * Assert that an entry identified by {@code dn} exists.
     *
     * @param dn The distinguished name.
     */
    public void assertDNExists(final String dn) {
        if (!verifyDNExists(dn)) {
            final StringBuilder message = new StringBuilder("Entry for DN: ");
            message.append(dn);
            message.append(" does not exist");
            throw new AssertionError(message);
        }
    }

    /**
     * Assert that the entry identified by {@code dn} is of type {@code objectclass}.
     *
     * @param dn          The distinguished name.
     * @param objectclass The type name.
     */
    public void assertDNIsA(final String dn,
                            final String objectclass) {
        if (!verifyDNIsA(dn, objectclass)) {
            final StringBuilder message = new StringBuilder("Entry for DN: ");
            message.append(dn);
            message.append(" is not of type: ");
            message.append(objectclass);
            throw new AssertionError(message);
        }
    }

    /**
     * Assert that the entry identified by {@code dn} has an attribute named {@code attributeName}.
     *
     * @param dn            The distinguished name.
     * @param attributeName The attribute name.
     */
    public void assertDNHasAttribute(final String dn,
                                     final String attributeName) {
        if (!verifyDNHasAttribute(dn, attributeName)) {
            final StringBuilder message = new StringBuilder("Entry for DN: ");
            message.append(dn);
            message.append(" does not have attribute: ");
            message.append(attributeName);
            throw new AssertionError(message);
        }
    }

    /**
     * Assert that the entry identified by {@code dn} has an attribute named {@code attributeName} with
     * the attribute value(s) {@code attributeName}.
     *
     * @param dn             The distinguished name.
     * @param attributeName  The attribute name.
     * @param attributeValue The attribute value(s).
     */
    public void assertDNHasAttributeValue(final String dn,
                                          final String attributeName,
                                          final String... attributeValue) {
        if (!verifyDNHasAttributeValue(dn, attributeName, attributeValue)) {
            final StringBuilder message = new StringBuilder("Attribute named: ");
            message.append(attributeName);
            message.append(" for entry for DN: ");
            message.append(dn);
            message.append(" is does not match: ");
            message.append(arrayToString(attributeValue));
            throw new AssertionError(message);
        }
    }

    /**
     * Disconnect from the LDAP directory server.
     */
    public void disconnect() {
        connection.close();
    }

    /**
     * Close The directory tester by disconnecting from the LDAP directory server.
     *
     * @since 2.0.0
     */
    public void close() {
        disconnect();
    }

    /**
     * Check if {@code item} is present in {@code items} ignoring case.
     *
     * @param items An array of items.
     * @param item  The item being searched for.
     * @return {@code true} if {@code item} is present in {@code items}. Otherwise, {@code false}.
     */
    private boolean arrayContains(final String[] items,
                                  final String item) {
        for (final String value : items) {
            if (item.equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Convert an array of strings to string.
     *
     * @param items The array of strings.
     * @return The formatted string.
     */
    private String arrayToString(final String[] items) {
        final StringBuilder builder = new StringBuilder("[");
        if (items.length > 0) {
            builder.append(items[0]);
            for (int i = 1; i < items.length; i++) {
                builder.append(',');
                builder.append(items[i]);
            }
        }
        builder.append(']');
        return builder.toString();
    }
}
