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

import com.unboundid.ldap.listener.InMemoryDirectoryServer;
import com.unboundid.ldap.listener.InMemoryDirectoryServerConfig;
import com.unboundid.ldap.listener.InMemoryListenerConfig;
import com.unboundid.ldap.sdk.*;
import com.unboundid.ldif.LDIFChangeRecord;
import com.unboundid.ldif.LDIFException;
import com.unboundid.ldif.LDIFReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Helper functions to start the in-memory LDAP directory server, load LDAP directory entries from an LDIF files and
 * shut down the in-memory LDAP directory server.
 *
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 1.0.1
 */
public final class DirectoryServerUtils {

    /**
     * Create and configure an embedded LDAP directory server, load seed data and start the server.
     *
     * @param ldifFile The LDIF resource or file from which LDIF records will be loaded.
     * @return The {@link com.unboundid.ldap.listener.InMemoryDirectoryServer} object.
     * @throws com.unboundid.ldif.LDIFException     If there was an error in the LDIF data.
     * @throws com.unboundid.ldap.sdk.LDAPException If there was a problem configuring or starting the embedded LDAP directory server.
     * @throws java.io.IOException                  If there was a problem reading the LDIF data.
     */
    @Deprecated
    public static InMemoryDirectoryServer startServer(final int port,
                                                      final String baseDN,
                                                      final String authDN,
                                                      final String authPassword,
                                                      final String ldifFile)
            throws LDIFException, LDAPException, IOException {
        return startServer(port, baseDN, authDN, authPassword, new String[]{ldifFile});
    }

    /**
     * Create and configure an embedded LDAP directory server, load seed data and start the server.
     *
     * @param ldifFiles The LDIF resources or files from which LDIF records will be loaded.
     * @return The {@link com.unboundid.ldap.listener.InMemoryDirectoryServer} object.
     * @throws com.unboundid.ldif.LDIFException     If there was an error in the LDIF data.
     * @throws com.unboundid.ldap.sdk.LDAPException If there was a problem configuring or starting the embedded LDAP directory server.
     * @throws java.io.IOException                  If there was a problem reading the LDIF data.
     */
    public static InMemoryDirectoryServer startServer(final int port,
                                                      final String baseDN,
                                                      final String authDN,
                                                      final String authPassword,
                                                      final String[] ldifFiles)
            throws LDIFException, LDAPException, IOException {
        final InMemoryListenerConfig listenerConfig = InMemoryListenerConfig.createLDAPConfig("default", port);
        final InMemoryDirectoryServerConfig config = new InMemoryDirectoryServerConfig(new DN(baseDN));
        config.setListenerConfigs(listenerConfig);
        config.addAdditionalBindCredentials(authDN, authPassword);
        final InMemoryDirectoryServer server = new InMemoryDirectoryServer(config);
        server.add(new Entry(baseDN, new Attribute("objectclass", "domain", "top")));
        server.startListening();
        for (final String ldifFile : ldifFiles) {
            loadData(server, ldifFile);
        }
        return server;
    }

    /**
     * Load LDIF records from a file to seed the LDAP directory.
     *
     * @param server   The embedded LDAP directory server.
     * @param ldifFile The LDIF resource or file from which LDIF records will be loaded.
     * @throws com.unboundid.ldif.LDIFException     If there was an error in the LDIF data.
     * @throws com.unboundid.ldap.sdk.LDAPException If there was a problem loading the LDIF records into the LDAP directory.
     * @throws java.io.IOException                  If there was a problem reading the LDIF records from the file.
     */
    public static void loadData(final InMemoryDirectoryServer server,
                                final String ldifFile)
            throws LDIFException, LDAPException, IOException {
        if (ldifFile != null && !ldifFile.isEmpty()) {
            final InputStream inputStream;
            final File file = new File(ldifFile);
            if (file.exists()) {
                inputStream = new FileInputStream(file);
            } else {
                final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                inputStream = classLoader.getResourceAsStream(ldifFile);
            }
            loadData(server.getConnection(), inputStream);
        }
    }

    private static void loadData(final LDAPConnection connection,
                                 final InputStream inputStream)
            throws LDIFException, LDAPException, IOException {
        try {
            final LDIFReader reader = new LDIFReader(inputStream);
            LDIFChangeRecord changeRecord = reader.readChangeRecord(true);
            while (changeRecord != null) {
                changeRecord.processChange(connection);
                changeRecord = reader.readChangeRecord(true);
            }
        } finally {
            inputStream.close();
        }
    }

    /**
     * Shutdown the embedded LDAP directory server.
     *
     * @param server The embedded LDAP directory server.
     */
    public static void stopServer(final InMemoryDirectoryServer server) {
        server.shutDown(true);
    }
}