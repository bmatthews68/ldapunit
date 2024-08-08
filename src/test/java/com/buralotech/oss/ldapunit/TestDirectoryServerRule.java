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

import org.junit.Rule;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

/**
 * Unit test the {@link DirectoryServerRule} rule.
 *
 * @author <a href="mailto:bmatthews68@gmail.com">Brian Matthews</a>
 * @since 1.0.0
 */
@DirectoryServerConfiguration
public class TestDirectoryServerRule {

    /**
     * The rule being tested.
     */
    @Rule
    public DirectoryServerRule directoryServerRule = new DirectoryServerRule();

    /**
     * Verify that the {@link DirectoryServerRule} launched an embedded directory server.
     */
    @Test
    public void checkServerIsRunning() {
        final DirectoryTester tester = new DirectoryTester("localhost", DirectoryServerConfiguration.DEFAULT_PORT, "uid=admin,ou=system", "secret");
        try {
            tester.assertDNExists("dc=buralotech,dc=com");
        } finally {
            tester.disconnect();
        }
    }

    /**
     * Verify should return true if the DN exists and false if it does not.
     */
    @Test
    public void checkVerifyDNExists() {
        assertTrue(directoryServerRule.verifyDNExists("dc=buralotech,dc=com"));
        assertFalse(directoryServerRule.verifyDNExists("ou=People,dc=buralotech,dc=com"));
    }

    /**
     * Verify that the assertion succeeds when the DN does exist.
     */
    @Test
    public void checkAssertDNExistsSucceeds() {
        directoryServerRule.assertDNExists("dc=buralotech,dc=com");
    }

    /**
     * Verify that the assertion fails if the DN does not exist.
     */
    @Test
    public void checkAssertDNExistsFails() {
        assertThrows(
                AssertionError.class,
                () -> directoryServerRule.assertDNExists("ou=People,dc=buralotech,dc=com"));
    }

    /**
     * An exception is thrown when we try to verify an invalid DN.
     */
    @Test
    public void throwsExceptionIfInvalidDN() {
        assertThrows(
                DirectoryTesterException.class,
                () -> directoryServerRule.verifyDNExists("dc:buralotech,dc:com"));
    }

    /**
     * Verify that the {@link DirectoryTester#verifyDNHasAttribute(String, String)} behaves correctly.
     */
    @Test
    @DirectoryServerConfiguration(ldifFiles = "com/buralotech/oss/ldapunit/initial.ldif")
    public void checkVerifyDNHasAttribute() {
        assertTrue(directoryServerRule.verifyDNHasAttribute("dc=buralotech,dc=com", "dc"));
        assertFalse(directoryServerRule.verifyDNHasAttribute("dc=buralotech,dc=com", "ou"));
        assertFalse(directoryServerRule.verifyDNHasAttribute("ou=People,dc=buralotech,dc=com", "dc"));
        assertTrue(directoryServerRule.verifyDNHasAttribute("ou=People,dc=buralotech,dc=com", "ou"));
        assertFalse(directoryServerRule.verifyDNHasAttribute("ou=Groups,dc=buralotech,dc=com", "ou"));
    }

    /**
     * Verify that the {@link DirectoryTester#verifyDNIsA(String, String)} behaves correctly.
     */
    @Test
    @DirectoryServerConfiguration(ldifFiles = "com/buralotech/oss/ldapunit/initial.ldif")
    public void checkVerifyDNIsA() {
        assertTrue(directoryServerRule.verifyDNIsA("dc=buralotech,dc=com", "top"));
        assertTrue(directoryServerRule.verifyDNIsA("dc=buralotech,dc=com", "domain"));
        assertFalse(directoryServerRule.verifyDNIsA("dc=buralotech,dc=com", "organizationalUnit"));
        assertTrue(directoryServerRule.verifyDNIsA("ou=People,dc=buralotech,dc=com", "top"));
        assertTrue(directoryServerRule.verifyDNIsA("ou=People,dc=buralotech,dc=com", "organizationalUnit"));
        assertFalse(directoryServerRule.verifyDNIsA("ou=People,dc=buralotech,dc=com", "inetOrgPerson"));
        assertFalse(directoryServerRule.verifyDNIsA("ou=Groips,dc=buralotech,dc=com", "top"));
    }

    /**
     * Verify that the {@link DirectoryTester#verifyDNHasAttributeValue(String, String, String...)} behaves correctly.
     */
    @Test
    @DirectoryServerConfiguration(ldifFiles = "com/buralotech/oss/ldapunit/initial.ldif")
    public void checkVerifyDNHasAttributeValue() {
        assertTrue(directoryServerRule.verifyDNHasAttributeValue("dc=buralotech,dc=com", "objectclass", "top", "domain"));
        assertTrue(directoryServerRule.verifyDNHasAttributeValue("dc=buralotech,dc=com", "objectclass", "domain", "top"));
        assertTrue(directoryServerRule.verifyDNHasAttributeValue("dc=buralotech,dc=com", "dc", "buralotech"));
        assertFalse(directoryServerRule.verifyDNHasAttributeValue("dc=buralotech,dc=com", "dc", "com"));
        assertFalse(directoryServerRule.verifyDNHasAttributeValue("dc=buralotech,dc=com", "dc", "buralotech", "com"));
        assertFalse(directoryServerRule.verifyDNHasAttributeValue("dc=buralotech,dc=com", "ou", "People"));
    }

    /**
     * Verify that the {@link DirectoryTester#assertDNIsA(String, String)} method succeeds if the LDAP
     * directory entry is a member of the object class.
     */
    @Test
    public void assertDNIsAShouldSucceed() {
        directoryServerRule.assertDNIsA("dc=buralotech,dc=com", "domain");
    }

    /**
     * Verify that the {@link DirectoryTester#assertDNIsA(String, String)} method throws an exception if the LDAP
     * directory entry is not a member of the object class.
     */
    @Test
    public void assertDNIsAShouldFail() {
        assertThrows(
                AssertionError.class,
                () -> directoryServerRule.assertDNIsA("dc=buralotech,dc=com", "organizationalUnit"));
    }

    /**
     * Verify that the {@link DirectoryTester#assertDNHasAttribute(String, String)} method succeeds
     * if the LDAP directory entry has the named attribute.
     */
    @Test
    public void assertDNHasAttributeShouldSucceed() {
        directoryServerRule.assertDNHasAttribute("dc=buralotech,dc=com", "dc");
    }

    /**
     * Verify that the {@link DirectoryTester#assertDNHasAttribute(String, String)} method throws an exception
     * if the LDAP directory entry does not have the named attribute.
     */
    @Test
    public void assertDNHasAttributeShouldFail() {
        assertThrows(
                AssertionError.class,
                () -> directoryServerRule.assertDNHasAttribute("dc=buralotech,dc=com", "ou"));
    }

    /**
     * Verify that the {@link DirectoryTester#assertDNHasAttributeValue(String, String, String...)} method
     * succeeds if the LDAP directory entry has a matching name/value pair.
     */
    @Test
    public void assertDNHasAttributeValueShouldSucceed() {
        directoryServerRule.assertDNHasAttributeValue("dc=buralotech,dc=com", "dc", "buralotech");
    }

    /**
     * Verify that the {@link DirectoryTester#assertDNHasAttributeValue(String, String, String...)} method
     * throws an exception if the LDAP directory entry does not have the matching name/value pair.
     */
    @Test
    public void assertDNHasAttributeValueShouldFail() {
        assertThrows(
                AssertionError.class,
                () -> directoryServerRule.assertDNHasAttributeValue("dc=buralotech,dc=com", "dc", "com"));
    }

    /**
     * Verify that the server can be started with a custom schema.
     */
    @Test
    @DirectoryServerConfiguration(
            baseDN = "group-id=users",
            baseObjectClasses = "group",
            baseAttributes = {
                    "group-id=users",
                    "group-name=Users"
            },
            ldifFiles = "com/buralotech/oss/ldapunit/custom-data-without-default.ldif",
            schemaFiles = {"com/buralotech/oss/ldapunit/custom-schema.ldif"})
    public void assertCustomSchemaCanBeUsedOnItsOwn() {

        directoryServerRule.verifyDNExists("group-id=users");
        directoryServerRule.verifyDNIsA("group-id=users", "group");
        directoryServerRule.verifyDNHasAttributeValue("group-id=users", "group-id", "users");
        directoryServerRule.verifyDNHasAttributeValue("group-id=users", "group-name", "Users");

        directoryServerRule.verifyDNExists("user-id=brian,group-id=users");
        directoryServerRule.verifyDNIsA("user-id=brian,group-id=users", "user");
        directoryServerRule.verifyDNHasAttributeValue("user-id=brian,group-id=users", "user-id", "brian");
        directoryServerRule.verifyDNHasAttributeValue("user-id=brian,group-id=users", "user-name", "Brian");
    }

    /**
     * Verify that the server can be started with a default and custom schema.
     */
    @Test
    @DirectoryServerConfiguration(
            ldifFiles = "com/buralotech/oss/ldapunit/custom-data-with-default.ldif",
            schemaFiles = {"default", "com/buralotech/oss/ldapunit/custom-schema.ldif"})
    public void assertCustomSchemaCanBeUsedWitDefault() {

        directoryServerRule.verifyDNExists("group-id=users,dc=buralotech,dc=com");
        directoryServerRule.verifyDNIsA("group-id=users,dc=buralotech,dc=com", "group");
        directoryServerRule.verifyDNHasAttributeValue("group-id=users,dc=buralotech,dc=com", "group-id", "users");
        directoryServerRule.verifyDNHasAttributeValue("group-id=users,dc=buralotech,dc=com", "group-name", "Users");

        directoryServerRule.verifyDNExists("user-id=brian,group-id=users,dc=buralotech,dc=com");
        directoryServerRule.verifyDNIsA("user-id=brian,group-id=users,dc=buralotech,dc=com", "user");
        directoryServerRule.verifyDNHasAttributeValue("user-id=brian,group-id=users,dc=buralotech,dc=com", "user-id", "brian");
        directoryServerRule.verifyDNHasAttributeValue("user-id=brian,group-id=users,dc=buralotech,dc=com", "user-name", "Brian");
    }
}
