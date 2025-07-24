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

import com.unboundid.ldap.listener.InMemoryDirectoryServer;
import com.unboundid.ldap.listener.InMemoryDirectoryServerConfig;
import com.unboundid.ldap.listener.InMemoryListenerConfig;
import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.DN;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.schema.Schema;
import com.unboundid.ldif.LDIFChangeRecord;
import com.unboundid.ldif.LDIFException;
import com.unboundid.ldif.LDIFReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Helper functions to start the in-memory LDAP directory server, load LDAP directory entries from an LDIF files and
 * shut down the in-memory LDAP directory server.
 *
 * @author <a href="mailto:bmatthews68@gmail.com">Brian Matthews</a>
 * @since 1.0.1
 */
final class DirectoryServerUtils {

    /**
     * Hidden constructor.
     */
    private DirectoryServerUtils() {
    }

    /**
     * Start the directory server using the configuration specified by the {@link DirectoryServerConfiguration}
     * annotation.
     *
     * @param annotation The configuration.
     * @return The {@link  InMemoryDirectoryServer} object.
     * @throws LDIFException If there was an error in the LDIF data.
     * @throws LDAPException If there was a problem configuring or starting the embedded LDAP directory server.
     * @throws IOException   If there was a problem reading the LDIF data.
     */
    static InMemoryDirectoryServer startServer(final DirectoryServerConfiguration annotation)
            throws LDIFException, LDAPException, IOException {
        return startServer(
                annotation.port(),
                annotation.baseDN(),
                annotation.baseObjectClasses(),
                annotation.baseAttributes(),
                annotation.authDN(),
                annotation.authPassword(),
                annotation.ldifFiles(),
                annotation.schemaFiles());
    }

    /**
     * Create and configure an embedded LDAP directory server, load seed data and start the server.
     *
     * @param port              The TCP port that the LDAP directory server will be configured to listen on.
     * @param baseDN            The DN that will be configured as the root of the LDAP directory.
     * @param baseObjectClasses The object classes to use when creating the base DN.
     * @param baseAttributes    The attributes to set on the base DN.
     * @param authDN            The DN that will be configured as the administrator account identifier.
     * @param authPassword      The password that will be configured as the authentication credentials for
     *                          the administrator account.
     * @param ldifFiles         The LDIF resources or files from which LDIF records will be loaded.
     * @param schemaFiles       The files from which to load custom schemas.
     * @return The {@link  InMemoryDirectoryServer} object.
     * @throws LDIFException If there was an error in the LDIF data.
     * @throws LDAPException If there was a problem configuring or starting the embedded LDAP directory server.
     * @throws IOException   If there was a problem reading the LDIF data.
     */
    static InMemoryDirectoryServer startServer(final int port,
                                               final String baseDN,
                                               final String[] baseObjectClasses,
                                               final String[] baseAttributes,
                                               final String authDN,
                                               final String authPassword,
                                               final String[] ldifFiles,
                                               final String[] schemaFiles)
            throws LDIFException, LDAPException, IOException {
        final InMemoryListenerConfig listenerConfig = InMemoryListenerConfig.createLDAPConfig("default", port);
        final InMemoryDirectoryServerConfig config = new InMemoryDirectoryServerConfig(new DN(baseDN));
        loadSchema(config, schemaFiles);
        config.setListenerConfigs(listenerConfig);
        config.addAdditionalBindCredentials(authDN, authPassword);
        final InMemoryDirectoryServer server = new InMemoryDirectoryServer(config);
        final int n = baseAttributes.length;
        final Attribute[] attributes = new Attribute[1 + n];
        for (int i = 0; i < n; i++) {
            final int j = baseAttributes[i].indexOf('=');
            final String name;
            final String value;
            if (j == -1) {
                name = baseAttributes[i];
                value = "";
            } else {
                name = baseAttributes[i].substring(0, j);
                value = baseAttributes[i].substring(j + 1);
            }
            attributes[i] = new Attribute(name, value);
        }
        attributes[n] = new Attribute("objectclass", baseObjectClasses);
        server .add(new Entry(baseDN, attributes));
        server.startListening();
        for (final String ldifFile : ldifFiles) {
            loadData(server, ldifFile);
        }
        return server;
    }

    /**
     * Look for path on the classpath or file system and return an {@link InputStream} if found.
     *
     * @param path The path.
     * @return An {@link InputStream} if the path exists on the classpath or file sytem. Otherwise, {@code null}.
     */
    private static InputStream getInputStream(final String path) {
        if (path == null || path.isEmpty()) {
            return null;
        }
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        final InputStream inputStream = classLoader.getResourceAsStream(path);
        if (inputStream == null) {
            final File file = new File(path);
            if (file.exists()) {
                try {
                    return new FileInputStream(file);
                } catch (final FileNotFoundException e) {
                    return null;
                }
            } else {
                return null;
            }
        } else {
            return inputStream;
        }
    }

    /**
     * Load a custom schema.
     *
     * @param config      The directory server configuration.
     * @param schemaFiles The schema files.
     * @throws LDIFException If there was a problem loading the schema into the LDAP directory.
     * @throws LDAPException If there was a problem loading the schema into the LDAP directory.
     * @throws IOException   If there was a problem reading the schema from the file.
     */
    private static void loadSchema(final InMemoryDirectoryServerConfig config,
                                   final String[] schemaFiles)
            throws LDIFException, LDAPException, IOException {
        if (schemaFiles.length > 0) {
            final Schema[] schemas = new Schema[schemaFiles.length];
            for (int i = 0; i < schemaFiles.length; i++) {
                if ("default".equals(schemaFiles[i])) {
                    schemas[i] = Schema.getDefaultStandardSchema();
                } else {
                    try (InputStream inputStream = getInputStream(schemaFiles[i])) {
                        if (inputStream != null) {
                            schemas[i] = Schema.getSchema(inputStream);
                        }
                    }
                }
            }
            config.setSchema(Schema.mergeSchemas(schemas));
        }
    }

    /**
     * Load LDIF records from a file to seed the LDAP directory.
     *
     * @param server   The embedded LDAP directory server.
     * @param ldifFile The LDIF resource or file from which LDIF records will be loaded.
     * @throws LDIFException If there was an error in the LDIF data.
     * @throws LDAPException If there was a problem loading the LDIF records into the LDAP directory.
     * @throws IOException   If there was a problem reading the LDIF records from the file.
     */
    private static void loadData(final InMemoryDirectoryServer server,
                                 final String ldifFile)
            throws LDIFException, LDAPException, IOException {
        try (InputStream inputStream = getInputStream(ldifFile)) {
            if (inputStream != null) {
                loadData(server.getConnection(), inputStream);
            }
        }
    }

    /**
     * Load LDIF records from an input stream to seed the LDAP directory.
     *
     * @param connection  A connection to the LDAP directory.
     * @param inputStream TThe input stream from which LDIF records will be loaded.
     * @throws LDIFException If there was an error in the LDIF data.
     * @throws LDAPException If there was a problem loading the LDIF records into the LDAP directory.
     * @throws IOException   If there was a problem reading the LDIF records from the file.
     */
    private static void loadData(final LDAPConnection connection,
                                 final InputStream inputStream)
            throws LDIFException, LDAPException, IOException {
        try (LDIFReader reader = new LDIFReader(inputStream)) {
            LDIFChangeRecord changeRecord = reader.readChangeRecord(true);
            while (changeRecord != null) {
                changeRecord.processChange(connection);
                changeRecord = reader.readChangeRecord(true);
            }
        }
    }

    /**
     * Shutdown the embedded LDAP directory server.
     *
     * @param server The embedded LDAP directory server.
     */
    static void stopServer(final InMemoryDirectoryServer server) {
        server.shutDown(true);
    }
}
