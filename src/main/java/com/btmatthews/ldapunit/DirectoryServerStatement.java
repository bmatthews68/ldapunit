/*
 * Copyright 2013-2021 Brian Thomas Matthews
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
import org.junit.runners.model.Statement;

/**
 * A {@link Statement} wrapper that launches an embedded LDAP directory server before executing the wrapped statement.
 * The embedded LDAP directory server is shutdown after the wrapped statement has been executed.
 *
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 1.0.0
 */
final class DirectoryServerStatement extends Statement {

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
    DirectoryServerStatement(final Statement stmt, final DirectoryServerConfiguration cfg) {
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
        try (final InMemoryDirectoryServer server = DirectoryServerUtils.startServer(
                annotation.port(), annotation.baseDN(), annotation.authDN(),
                annotation.authPassword(), annotation.ldifFiles())) {
            base.evaluate();
        }
    }
}
