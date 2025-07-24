/*
 * Copyright 2021-2025 Brian Thomas Matthews
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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test the {@link DirectoryServerExtension} extension.
 *
 * @author <a href="mailto:bmatthews68@gmail.com">Brian Matthews</a>
 * @since 1.2.0
 */
@ExtendWith(DirectoryServerExtension.class)
@DirectoryServerConfiguration
public class TestDirectoryServerExtension {

    /**
     * Verify that the {@link DirectoryServerRule} launched an embedded directory server.
     *
     * @param tester Used to perform assertions.
     */
    @Test
    void checkServerIsRunning(final DirectoryTester tester) {
        tester.assertDNExists("dc=buralotech,dc=com");
    }

    /**
     * Verify should return true if the DN exists and false if it does not.
     *
     * @param tester Used to perform assertions.
     */
    @Test
    void checkVerifyDNExists(final DirectoryTester tester) {
        assertTrue(tester.verifyDNExists("dc=buralotech,dc=com"));
        assertFalse(tester.verifyDNExists("ou=People,dc=buralotech,dc=com"));
    }

    /**
     * Verify that the assertion succeeds when the DN does exist.
     *
     * @param tester Used to perform assertions.
     */
    @Test
    void checkAssertDNExistsSucceeds(final DirectoryTester tester) {
        tester.assertDNExists("dc=buralotech,dc=com");
    }

    /**
     * Verify that the assertion fails if the DN does not exist.
     *
     * @param tester Used to perform assertions.
     */
    @Test
    void checkAssertDNExistsFails(final DirectoryTester tester) {
        assertThrows(
                AssertionError.class,
                () -> tester.assertDNExists("ou=People,dc=buralotech,dc=com"));
    }

    /**
     * An exception is thrown when we try to verify an invalid DN.
     *
     * @param tester Used to perform assertions.
     */
    @Test
    void throwsExceptionIfInvalidDN(final DirectoryTester tester) {
        assertThrows(
                DirectoryTesterException.class,
                () -> tester.verifyDNExists("dc:buralotech,dc:com"));
    }

    /**
     * Verify that the {@link DirectoryTester#verifyDNHasAttribute(String, String)} behaves correctly.
     *
     * @param tester Used to perform assertions.
     */
    @Test
    @DirectoryServerConfiguration(ldifFiles = "com/buralotech/oss/ldapunit/initial.ldif")
    void checkVerifyDNHasAttribute(final DirectoryTester tester) {
        assertTrue(tester.verifyDNHasAttribute("dc=buralotech,dc=com", "dc"));
        assertFalse(tester.verifyDNHasAttribute("dc=buralotech,dc=com", "ou"));
        assertFalse(tester.verifyDNHasAttribute("ou=People,dc=buralotech,dc=com", "dc"));
        assertTrue(tester.verifyDNHasAttribute("ou=People,dc=buralotech,dc=com", "ou"));
        assertFalse(tester.verifyDNHasAttribute("ou=Groups,dc=buralotech,dc=com", "ou"));
    }

    /**
     * Verify that the {@link DirectoryTester#verifyDNIsA(String, String)} behaves correctly.
     *
     * @param tester Used to perform assertions.
     */
    @Test
    @DirectoryServerConfiguration(ldifFiles = "com/buralotech/oss/ldapunit/initial.ldif")
    void checkVerifyDNIsA(final DirectoryTester tester) {
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
     *
     * @param tester Used to perform assertions.
     */
    @Test
    @DirectoryServerConfiguration(ldifFiles = "com/buralotech/oss/ldapunit/initial.ldif")
    void checkVerifyDNHasAttributeValue(final DirectoryTester tester) {
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
     *
     * @param tester Used to perform assertions.
     */
    @Test
    void assertDNIsAShouldSucceed(final DirectoryTester tester) {
        tester.assertDNIsA("dc=buralotech,dc=com", "domain");
    }

    /**
     * Verify that the {@link DirectoryTester#assertDNIsA(String, String)} method throws an exception if the LDAP
     * directory entry is not a member of the object class.
     *
     * @param tester Used to perform assertions.
     */
    @Test
    void assertDNIsAShouldFail(final DirectoryTester tester) {
        assertThrows(
                AssertionError.class,
                () -> tester.assertDNIsA("dc=buralotech,dc=com", "organizationalUnit"));
    }

    /**
     * Verify that the {@link DirectoryTester#assertDNHasAttribute(String, String)} method succeeds
     * if the LDAP directory entry has the named attribute.
     *
     * @param tester Used to perform assertions.
     */
    @Test
    void assertDNHasAttributeShouldSucceed(final DirectoryTester tester) {
        tester.assertDNHasAttribute("dc=buralotech,dc=com", "dc");
    }

    /**
     * Verify that the {@link DirectoryTester#assertDNHasAttribute(String, String)} method throws an exception
     * if the LDAP directory entry does not have the named attribute.
     *
     * @param tester Used to perform assertions.
     */
    @Test
    void assertDNHasAttributeShouldFail(final DirectoryTester tester) {
        assertThrows(
                AssertionError.class,
                () -> tester.assertDNHasAttribute("dc=buralotech,dc=com", "ou"));
    }

    /**
     * Verify that the {@link DirectoryTester#assertDNHasAttributeValue(String, String, String...)} method
     * succeeds if the LDAP directory entry has a matching name/value pair.
     *
     * @param tester Used to perform assertions.
     */
    @Test
    void assertDNHasAttributeValueShouldSucceed(final DirectoryTester tester) {
        tester.assertDNHasAttributeValue("dc=buralotech,dc=com", "dc", "buralotech");
    }

    /**
     * Verify that the {@link DirectoryTester#assertDNHasAttributeValue(String, String, String...)} method
     * throws an exception if the LDAP directory entry does not have the matching name/value pair.
     *
     * @param tester Used to perform assertions.
     */
    @Test
    void assertDNHasAttributeValueShouldFail(final DirectoryTester tester) {
        assertThrows(
                AssertionError.class,
                () -> tester.assertDNHasAttributeValue("dc=buralotech,dc=com", "dc", "com"));
    }

    /**
     * Verify that the server can be started with a custom schema.
     *
     * @param tester Used to perform assertions.
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
    void assertCustomSchemaCanBeUsedOnItsOwn(final DirectoryTester tester) {

        assertTrue(tester.verifyDNExists("group-id=users"));
        assertTrue(tester.verifyDNIsA("group-id=users", "group"));
        assertTrue(tester.verifyDNHasAttributeValue("group-id=users", "group-id", "users"));
        assertTrue(tester.verifyDNHasAttributeValue("group-id=users", "group-name", "Users"));

        assertTrue(tester.verifyDNExists("user-id=brian,group-id=users"));
        assertTrue(tester.verifyDNIsA("user-id=brian,group-id=users", "user"));
        assertTrue(tester.verifyDNHasAttributeValue("user-id=brian,group-id=users", "user-id", "brian"));
        assertTrue(tester.verifyDNHasAttributeValue("user-id=brian,group-id=users", "user-name", "Brian"));
    }

    /**
     * Verify that the server can be started with a default and custom schema.
     *
     * @param tester Used to perform assertions.
     */
    @Test
    @DirectoryServerConfiguration(
            ldifFiles = "com/buralotech/oss/ldapunit/custom-data-with-default.ldif",
            schemaFiles = {"default", "com/buralotech/oss/ldapunit/custom-schema.ldif"})
    void assertCustomSchemaCanBeUsedWitDefault(final DirectoryTester tester) {

        assertTrue(tester.verifyDNExists("group-id=users,dc=buralotech,dc=com"));
        assertTrue(tester.verifyDNIsA("group-id=users,dc=buralotech,dc=com", "group"));
        assertTrue(tester.verifyDNHasAttributeValue("group-id=users,dc=buralotech,dc=com", "group-id", "users"));
        assertTrue(tester.verifyDNHasAttributeValue("group-id=users,dc=buralotech,dc=com", "group-name", "Users"));

        assertTrue(tester.verifyDNExists("user-id=brian,group-id=users,dc=buralotech,dc=com"));
        assertTrue(tester.verifyDNIsA("user-id=brian,group-id=users,dc=buralotech,dc=com", "user"));
        assertTrue(tester.verifyDNHasAttributeValue("user-id=brian,group-id=users,dc=buralotech,dc=com", "user-id", "brian"));
        assertTrue(tester.verifyDNHasAttributeValue("user-id=brian,group-id=users,dc=buralotech,dc=com", "user-name", "Brian"));
    }
}
