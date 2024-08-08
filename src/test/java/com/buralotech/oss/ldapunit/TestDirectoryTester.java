/*
 * Copyright 2013-2024 Brian Thomas Matthews
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

/**
 * Unit test {@link DirectoryTester}.
 *
 * @author <a href="mailto:bmatthews68@gmail.com">Brian Matthews</a>
 * @since 1.0.0
 */
@DirectoryServerConfiguration
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
     * Verify should return true if the DN exists and false if it does not.
     */
    @Test
    public void checkVerifyDNExists() {
        assertTrue(tester.verifyDNExists("dc=buralotech,dc=com"));
        assertFalse(tester.verifyDNExists("ou=People,dc=buralotech,dc=com"));
    }

    /**
     * Verify that the assertion succeeds when the DN does exist.
     */
    @Test
    public void checkAssertDNExistsSucceeds() {
        tester.assertDNExists("dc=buralotech,dc=com");
    }

    /**
     * Verify that the assertion fails if the DN does not exist.
     */
    @Test
    public void checkAssertDNExistsFails() {
        assertThrows(
                AssertionError.class,
                () -> tester.assertDNExists("ou=People,dc=buralotech,dc=com"));
    }

    /**
     * An exception is thrown when we try to verify an invalid DN.
     */
    @Test
    public void throwsExceptionIfInvalidDN() {
        assertThrows(
                DirectoryTesterException.class,
                () -> tester.verifyDNExists("dc:buralotech,dc:com"));
    }

    /**
     * Verify that the {@link DirectoryTester#verifyDNHasAttribute(String, String)} behaves correctly.
     */
    @Test
    @DirectoryServerConfiguration(ldifFiles = "com/buralotech/oss/ldapunit/initial.ldif")
    public void checkVerifyDNHasAttribute() {
        assertTrue(tester.verifyDNHasAttribute("dc=buralotech,dc=com", "dc"));
        assertFalse(tester.verifyDNHasAttribute("dc=buralotech,dc=com", "ou"));
        assertFalse(tester.verifyDNHasAttribute("ou=People,dc=buralotech,dc=com", "dc"));
        assertTrue(tester.verifyDNHasAttribute("ou=People,dc=buralotech,dc=com", "ou"));
        assertFalse(tester.verifyDNHasAttribute("ou=Groups,dc=buralotech,dc=com", "ou"));
    }

    /**
     * Verify that the {@link DirectoryTester#verifyDNIsA(String, String)} behaves correctly.
     */
    @Test
    @DirectoryServerConfiguration(ldifFiles = "com/buralotech/oss/ldapunit/initial.ldif")
    public void checkVerifyDNIsA() {
        assertTrue(tester.verifyDNIsA("dc=buralotech,dc=com", "top"));
        assertTrue(tester.verifyDNIsA("dc=buralotech,dc=com", "domain"));
        assertFalse(tester.verifyDNIsA("dc=buralotech,dc=com", "organizationalUnit"));
        assertTrue(tester.verifyDNIsA("ou=People,dc=buralotech,dc=com", "top"));
        assertTrue(tester.verifyDNIsA("ou=People,dc=buralotech,dc=com", "organizationalUnit"));
        assertFalse(tester.verifyDNIsA("ou=People,dc=buralotech,dc=com", "inetOrgPerson"));
        assertFalse(tester.verifyDNIsA("ou=Groips,dc=buralotech,dc=com", "top"));
    }

    /**
     * Verify that the {@link DirectoryTester#verifyDNHasAttributeValue(String, String, String...)} behaves correctly.
     */
    @Test
    @DirectoryServerConfiguration(ldifFiles = "com/buralotech/oss/ldapunit/initial.ldif")
    public void checkVerifyDNHasAttributeValue() {
        assertTrue(tester.verifyDNHasAttributeValue("dc=buralotech,dc=com", "objectclass", "top", "domain"));
        assertTrue(tester.verifyDNHasAttributeValue("dc=buralotech,dc=com", "objectclass", "domain", "top"));
        assertTrue(tester.verifyDNHasAttributeValue("dc=buralotech,dc=com", "dc", "buralotech"));
        assertFalse(tester.verifyDNHasAttributeValue("dc=buralotech,dc=com", "dc", "com"));
        assertFalse(tester.verifyDNHasAttributeValue("dc=buralotech,dc=com", "dc", "buralotech", "com"));
        assertFalse(tester.verifyDNHasAttributeValue("dc=buralotech,dc=com", "ou", "People"));
    }

    /**
     * Verify that the {@link DirectoryTester#assertDNIsA(String, String)} method succeeds if the LDAP
     * directory entry is a member of the object class.
     */
    @Test
    public void assertDNIsAShouldSucceed() {
        tester.assertDNIsA("dc=buralotech,dc=com", "domain");
    }

    /**
     * Verify that the {@link DirectoryTester#assertDNIsA(String, String)} method throws an exception if the LDAP
     * directory entry is not a member of the object class.
     */
    @Test
    public void assertDNIsAShouldFail() {
        assertThrows(
                AssertionError.class,
                () -> tester.assertDNIsA("dc=buralotech,dc=com", "organizationalUnit"));
    }

    /**
     * Verify that the {@link DirectoryTester#assertDNHasAttribute(String, String)} method succeeds
     * if the LDAP directory entry has the named attribute.
     */
    @Test
    public void assertDNHasAttributeShouldSucceed() {
        tester.assertDNHasAttribute("dc=buralotech,dc=com", "dc");
    }

    /**
     * Verify that the {@link DirectoryTester#assertDNHasAttribute(String, String)} method throws an exception
     * if the LDAP directory entry does not have the named attribute.
     */
    @Test
    public void assertDNHasAttributeShouldFail() {
        assertThrows(
                AssertionError.class,
                () -> tester.assertDNHasAttribute("dc=buralotech,dc=com", "ou"));
    }

    /**
     * Verify that the {@link DirectoryTester#assertDNHasAttributeValue(String, String, String...)} method
     * succeeds if the LDAP directory entry has a matching name/value pair.
     */
    @Test
    public void assertDNHasAttributeValueShouldSucceed() {
        tester.assertDNHasAttributeValue("dc=buralotech,dc=com", "dc", "buralotech");
    }

    /**
     * Verify that the {@link DirectoryTester#assertDNHasAttributeValue(String, String, String...)} method
     * throws an exception if the LDAP directory entry does not have the matching name/value pair.
     */
    @Test
    public void assertDNHasAttributeValueShouldFail() {
        assertThrows(
                AssertionError.class,
                () -> tester.assertDNHasAttributeValue("dc=buralotech,dc=com", "dc", "com"));
    }
}
