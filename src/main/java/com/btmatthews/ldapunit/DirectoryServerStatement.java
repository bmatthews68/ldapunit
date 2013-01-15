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
import org.junit.runners.model.Statement;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * A {@link Statement} wrapper that launches an embedded LDAP directory server before executing the wrapped statement.
 * The embedded LDAP directory server is shutdown after the wrapped statement has been executed.
 *
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 1.0.0
 */
public class DirectoryServerStatement extends Statement {

    /**
     * The wrapped statement {@link Statement}.
     */
    private final Statement base;
    /**
     * The annotation that defines the directory server configuration.
     */
    private final DirectoryServerConfiguration annotation;

    /**
     * Initialise the wrapper statement that starts an embedded LDAP directory server and shuts it down before and after
     * executing the wrapped statement.
     *
     * @param stmt The wrapped statement.
     * @param cfg  The directory server configuration.
     */
    public DirectoryServerStatement(final Statement stmt, final DirectoryServerConfiguration cfg) {
        base = stmt;
        annotation = cfg;
    }

    /**
     * Start an embedded LDAP directory server before executing the wrapped statement and shutdown the LDAP directory
     * server after the wrapped statement completes.
     *
     * @throws Throwable If there was an error starting the server, executing the wrapped statement or shutting the
     *                   wrapped server.
     */
    @Override
    public void evaluate() throws Throwable {
        final InMemoryDirectoryServer server = startServer();
        try {
            base.evaluate();
        } finally {
            stopServer(server);
        }
    }

    /**
     * Create and configure an embedded LDAP directory server, load seed data and start the server.
     *
     * @return The {@link InMemoryDirectoryServer} object.
     * @throws LDAPException If there was a problem configuring or starting the embedded LDAP directory server.
     * @throws IOException   If there was a problem reading the LDIF data.
     */
    private InMemoryDirectoryServer startServer() throws LDAPException, IOException {
        final InMemoryListenerConfig listenerConfig = InMemoryListenerConfig.createLDAPConfig("default", annotation.port());
        final InMemoryDirectoryServerConfig config = new InMemoryDirectoryServerConfig(new DN(annotation.baseDN()));
        config.setListenerConfigs(listenerConfig);
        config.addAdditionalBindCredentials(annotation.authDN(), annotation.authPassword());
        final InMemoryDirectoryServer server = new InMemoryDirectoryServer(config);
        server.add(new Entry(annotation.baseDN(), new Attribute("objectclass", "domain", "top")));
        loadData(server);
        server.startListening();
        return server;
    }

    /**
     * Load LDIF records from a file to seed the LDAP directory.
     *
     * @param server The embedded LDAP directory server.
     * @throws LDAPException If there was a problem loading the LDIF records into the LDAP directory.
     * @throws IOException   If there was a problem reading the LDIF records from the file.
     */
    private void loadData(final InMemoryDirectoryServer server) throws LDAPException, IOException {
        final String ldifFile = annotation.ldifFile();
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
    private void stopServer(final InMemoryDirectoryServer server) {
        server.shutDown(true);
    }
}
