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
 * Created with IntelliJ IDEA.
 * User: Brian
 * Date: 15/01/13
 * Time: 11:42
 * To change this template use File | Settings | File Templates.
 */
public class DirectoryServerStatement extends Statement {

    private final Statement base;
    private final DirectoryServerConfiguration annotation;

    public DirectoryServerStatement(final Statement stmt, final DirectoryServerConfiguration cfg) {
        base = stmt;
        annotation = cfg;
    }

    @Override
    public void evaluate() throws Throwable {
        final InMemoryDirectoryServer server = startServer();
        try {
            base.evaluate();
        } finally {
            stopServer(server);
        }
    }

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

    private void stopServer(final InMemoryDirectoryServer server) {
        server.shutDown(true);
    }
}
