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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Unit test that {@link DirectoryTester} can connect to a server within the retry count and timeout period if the
 * server start is delayed.
 *
 * @author <a href="mailto:bmatthews68@gmail.com">Brian Matthews</a>
 * @since 1.0.1
 */
public class TestDirectoryTesterConnection {

    /**
     * The root DN.
     */
    private static final String ROOT_DN = "dc=buralotech,dc=com";

    /**
     * The object classes for the root DN.
     */
    private static final String[] ROOT_OBJECTCLASSES = {"domain", "top"};

    /**
     * The authentication DN.
     */
    private static final String AUTH_DN = "uid=admin,ou=system";

    /**
     * The authentication password.
     */
    private static final String AUTH_PASSWORD = "secret";

    /**
     * The in-memory LDAP directory server.
     */
    private InMemoryDirectoryServer server = null;

    /**
     * Clean up after test case execution by shutting down the in-memory LDAP directory server.
     */
    @AfterEach
    public void tearDown() {
        if (server != null) {
            DirectoryServerUtils.stopServer(server);
        }
    }

    /**
     * Verify that {@link DirectoryTester} can connect to a server within the retry count and timeout period if the
     * server start is delayed.
     */
    @Test
    public void retriesConnection() {
        final ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();
        timer.schedule(() -> {
            try {
                server = DirectoryServerUtils.startServer(DirectoryServerConfiguration.DEFAULT_PORT, ROOT_DN,
                        ROOT_OBJECTCLASSES, new String[0], AUTH_DN, AUTH_PASSWORD, new String[0], new String[0]);
            } catch (final Exception e) {
            }
        }, 10L, TimeUnit.SECONDS);

        try ( DirectoryTester tester = new DirectoryTester()) {
            tester.assertDNExists(ROOT_DN);
        }
    }
}
