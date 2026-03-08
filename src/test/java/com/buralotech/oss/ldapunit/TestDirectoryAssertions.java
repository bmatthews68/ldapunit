/*
 * Copyright 2026 Brian Thomas Matthews
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

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit test {@link DirectoryTester}.
 *
 * @author <a href="mailto:bmatthews68@gmail.com">Brian Matthews</a>
 * @since 1.0.0
 */
@DirectoryServerConfiguration
public class TestDirectoryAssertions {

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
        tester = new DirectoryTester("localhost", DirectoryServerConfiguration.DEFAULT_PORT, "uid=admin,ou=system", "secret");
    }

    /**
     * Clean up after executing the test case by disconnecting from the LDAP directory server.
     */
    @After
    public void tearDown() {
        tester.disconnect();
    }

    /**
     * Verify that the assertion succeeds when the DN does exist.
     */
    @Test
    public void checkAssertDNExistsSucceeds() {
        tester.assertThat().exists("dc=buralotech,dc=com");
    }

    /**
     * Verify that the assertion fails if the DN does not exist.
     */
    @Test
    public void checkAssertDNExistsFails() {
        assertThrows(
                AssertionError.class,
                () -> tester.assertThat().exists("ou=People,dc=buralotech,dc=com"));
    }

    /**
     * Verify that the {@link DirectoryTester#assertDNIsA(String, String)} method succeeds if the LDAP
     * directory entry is a member of the object class.
     */
    @Test
    public void assertDNIsAShouldSucceed() {
        tester.assertThat().isA("dc=buralotech,dc=com", "domain");
    }

    /**
     * Verify that the {@link DirectoryTester#assertDNIsA(String, String)} method throws an exception if the LDAP
     * directory entry is not a member of the object class.
     */
    @Test
    public void assertDNIsAShouldFail() {
        assertThrows(
                AssertionError.class,
                () -> tester.assertThat().isA("dc=buralotech,dc=com", "organizationalUnit"));
    }

    /**
     * Verify that the {@link DirectoryTester#assertDNHasAttribute(String, String)} method succeeds
     * if the LDAP directory entry has the named attribute.
     */
    @Test
    public void assertDNHasAttributeShouldSucceed() {
        tester.assertThat().hasAttribute("dc=buralotech,dc=com", "dc");
    }

    /**
     * Verify that the {@link DirectoryTester#assertDNHasAttribute(String, String)} method throws an exception
     * if the LDAP directory entry does not have the named attribute.
     */
    @Test
    public void assertDNHasAttributeShouldFail() {
        assertThrows(
                AssertionError.class,
                () -> tester.assertThat().hasAttribute("dc=buralotech,dc=com", "ou"));
    }

    /**
     * Verify that the {@link DirectoryTester#assertDNHasAttributeValue(String, String, String...)} method
     * succeeds if the LDAP directory entry has a matching name/value pair.
     */
    @Test
    public void assertDNHasAttributeValueShouldSucceed() {
        tester.assertThat().hasAttributeValue("dc=buralotech,dc=com", "dc", "buralotech");
    }

    /**
     * Verify that the {@link DirectoryTester#assertDNHasAttributeValue(String, String, String...)} method
     * throws an exception if the LDAP directory entry does not have the matching name/value pair.
     */
    @Test
    public void assertDNHasAttributeValueShouldFail() {
        assertThrows(
                AssertionError.class,
                () -> tester.assertThat().hasAttributeValue("dc=buralotech,dc=com", "dc", "com"));
    }
}
