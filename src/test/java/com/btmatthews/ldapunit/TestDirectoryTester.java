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

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit test {@link DirectoryTester}.
 *
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 1.0.0
 */
public class TestDirectoryTester {

    /**
     * This rule launches and shuts down the in-memory LDAP directory server.
     */
    @Rule
    public DirectoryServerRule directoryServerRule = new DirectoryServerRule();
    /**
     * The {@link DirectoryTester} being tested.
     */
    private DirectoryTester tester;

    /**
     * Prepare for test case execution by connecting to the LDAP directory.
     */
    @Before
    public void setUp() {
        tester = new DirectoryTester("localhost", 10389, "uid=admin,ou=system", "secret");
    }

    /**
     * Clean up after executing the test case by disconnecting from the LDAP directory server.
     */
    @After
    public void tearDown() {
        tester.disconnect();
    }

    /**
     * Verify should return true if the DN exists and false if it does not.
     */
    @Test
    @DirectoryServerConfiguration
    public void checkVerifyDNExists() {
        assertTrue(tester.verifyDNExists("dc=btmatthews,dc=com"));
        assertFalse(tester.verifyDNExists("ou=People,dc=btmatthews,dc=com"));
    }

    /**
     * Verify that the assertion succeeds when the DN does exist.
     */
    @Test
    @DirectoryServerConfiguration
    public void checkAssertDNExistsSucceeds() {
        tester.assertDNExists("dc=btmatthews,dc=com");
    }

    /**
     * Verify that the assertion fails if the DN does not exist.
     */
    @Test(expected = AssertionError.class)
    @DirectoryServerConfiguration
    public void checkAssertDNExistsFails() {
        tester.assertDNExists("ou=People,dc=btmatthews,dc=com");
    }

    /**
     * An exception is thrown when we try to verify an invalid DN.
     */
    @Test(expected = DirectoryTesterException.class)
    @DirectoryServerConfiguration
    public void throwsExceptionIfInvalidDN() {
        tester.verifyDNExists("dc:btmatthews,dc:com");
    }
}
