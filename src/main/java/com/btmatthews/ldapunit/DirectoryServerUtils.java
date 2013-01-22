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
import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.DN;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldif.LDIFReader;

import java.io.File;
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
     * @throws com.unboundid.ldap.sdk.LDAPException
     *                             If there was a problem configuring or starting the embedded LDAP directory server.
     * @throws java.io.IOException If there was a problem reading the LDIF data.
     */
    public static InMemoryDirectoryServer startServer(final int port,
                                                      final String baseDN,
                                                      final String authDN,
                                                      final String authPassword,
                                                      final String ldifFile) throws LDAPException, IOException {
        final InMemoryListenerConfig listenerConfig = InMemoryListenerConfig.createLDAPConfig("default", port);
        final InMemoryDirectoryServerConfig config = new InMemoryDirectoryServerConfig(new DN(baseDN));
        config.setListenerConfigs(listenerConfig);
        config.addAdditionalBindCredentials(authDN, authPassword);
        final InMemoryDirectoryServer server = new InMemoryDirectoryServer(config);
        server.add(new Entry(baseDN, new Attribute("objectclass", "domain", "top")));
        loadData(server, ldifFile);
        server.startListening();
        return server;
    }

    /**
     * Load LDIF records from a file to seed the LDAP directory.
     *
     * @param server   The embedded LDAP directory server.
     * @param ldifFile The LDIF resource or file from which LDIF records will be loaded.
     * @throws com.unboundid.ldap.sdk.LDAPException
     *                             If there was a problem loading the LDIF records into the LDAP directory.
     * @throws java.io.IOException If there was a problem reading the LDIF records from the file.
     */
    public static void loadData(final InMemoryDirectoryServer server,
                                final String ldifFile) throws LDAPException, IOException {
        if (ldifFile != null && !ldifFile.isEmpty()) {
            if (new File(ldifFile).exists()) {
                server.importFromLDIF(false, ldifFile);
            } else {
                final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                final InputStream inputStream = classLoader.getResourceAsStream(ldifFile);
                try {
                    final LDIFReader reader = new LDIFReader(inputStream);
                    server.importFromLDIF(false, reader);
                } finally {
                    inputStream.close();
                }
            }
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