/*
 * Copyright 2013-2016 Brian Thomas Matthews
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
import org.junit.After;
import org.junit.Test;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Unit test that {@link DirectoryTester} can connect to a server within the retry count and timeout period if the
 * server start is delayed.
 *
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 1.0.1
 */
public class TestDirectoryTesterConnection {

    /**
     * The LDAP port.
     */
    private static final int LDAP_PORT = 10389;
    /**
     * The root DN.
     */
    private static final String ROOT_DN = "dc=btmatthews,dc=com";
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
    @After
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
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    server = DirectoryServerUtils.startServer(LDAP_PORT, ROOT_DN, AUTH_DN, AUTH_PASSWORD, new String[0]);
                } catch (final Exception e) {
                }
            }
        }, 10000L);

        final DirectoryTester tester = new DirectoryTester();
        try {
            tester.assertDNExists(ROOT_DN);
        } finally {
            tester.disconnect();
        }
    }
}
