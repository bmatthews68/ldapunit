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

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * A JUnit test rule that starts an embedded LDAP directory server. The configuration for the directory server
 * is obtained from the {@link DirectoryServerConfiguration} annotation applied to either the test method or the test
 * class.
 *
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 1.0.0
 */
public class DirectoryServerRule implements TestRule {

    /**
     * The current configuration for the in-memory LDAP directory server.
     * @since 1.0.2
     */
    private DirectoryServerConfiguration annotation;

    /**
     * Modifies the method-running {@link Statement} to implement this test-running rule. The configuration for
     * the embedded LDAP directory server is obtained from the {@link DirectoryServerConfiguration} annotation that was
     * applied to either the test class or test method.
     *
     * @param base        The {@link Statement} to be modified
     * @param description A {@link Description} of the test implemented in {@code base}
     * @return If no configuration was found then {@code base} is returned. Otherwise, a new
     *         {@link DirectoryServerStatement} that wraps {@code base} is returned.
     */
    @Override
    public Statement apply(final Statement base, final Description description) {
        annotation = description.getAnnotation(DirectoryServerConfiguration.class);
        if (annotation == null) {
            final Class testClass = description.getTestClass();
            annotation = (DirectoryServerConfiguration) testClass.getAnnotation(DirectoryServerConfiguration.class);
        }
        if (annotation != null) {
            return new DirectoryServerStatement(base, annotation);
        }
        return base;
    }

    /**
     * Verify that an entry identified by {@code dn} exists.
     *
     * @param dn The distinguished name.
     * @return {@code true} if an entry identified by {@code dn} exists. Otherwise, {@code false} is returned.
     * @since 1.0.2
     */
    public boolean verifyDNExists(final String dn) {
        final DirectoryTester directoryTester = getDirectoryTester();
        try {
            return directoryTester.verifyDNExists(dn);

        } finally {
            directoryTester.disconnect();
        }
    }

    /**
     * Verify that the entry identified by {@code dn} is of type {@code objectclass}.
     *
     * @param dn          The distinguished name.
     * @param objectclass The type name.
     * @return {@code true} if an entry identified by {@code dn} exists and has attribute named {@code objectclass}.
     *         Otherwise, {@code false} is returned.
     * @since 1.0.2
     */
    public boolean verifyDNIsA(final String dn,
                               final String objectclass) {
        final DirectoryTester directoryTester = getDirectoryTester();
        try {
            return directoryTester.verifyDNIsA(dn, objectclass);

        } finally {
            directoryTester.disconnect();
        }
    }

    /**
     * Verify that the entry identified by {@code dn} has an attribute named {@code attributeName}.
     *
     * @param dn            The distinguished name.
     * @param attributeName The attribute name.
     * @return {@code true} if an entry identified by {@code dn} exists and has an attributed named
     *         {@code attributeName}. Otherwise, {@code false} is returned.
     * @since 1.0.2
     */
    public boolean verifyDNHasAttribute(final String dn,
                                        final String attributeName) {
        final DirectoryTester directoryTester = getDirectoryTester();
        try {
            return directoryTester.verifyDNHasAttribute(dn, attributeName);

        } finally {
            directoryTester.disconnect();
        }
    }

    /**
     * Verify that the entry identified by {@code dn} has an attribute named {@code attributeName} with
     * the attribute value(s) {@code attributeName}.
     *
     * @param dn             The distinguished name.
     * @param attributeName  The attribute name.
     * @param attributeValue The attribute value(s).
     * @return {@code true} if an antry identified by {@code dn} exists with an an attribute named {@code attributeName}
     *         that has value(s) {@code attributeValue}. Otherwise, {@code false} is returned.
     * @since 1.0.2
     */
    public boolean verifyDNHasAttributeValue(final String dn,
                                             final String attributeName,
                                             final String... attributeValue) {
        final DirectoryTester directoryTester = getDirectoryTester();
        try {
            return directoryTester.verifyDNHasAttributeValue(dn, attributeName, attributeValue);

        } finally {
            directoryTester.disconnect();
        }
    }

    /**
     * Assert that an entry identified by {@code dn} exists.
     *
     * @param dn The distinguished name.
     * @since 1.0.2
     */
    public void assertDNExists(final String dn) {
        final DirectoryTester directoryTester = getDirectoryTester();
        try {
            directoryTester.assertDNExists(dn);

        } finally {
            directoryTester.disconnect();
        }
    }

    /**
     * Assert that the entry identified by {@code dn} is of type {@code objectclass}.
     *
     * @param dn          The distinguished name.
     * @param objectclass The type name.
     * @since 1.0.2
     */
    public void assertDNIsA(final String dn,
                            final String objectclass) {
        final DirectoryTester directoryTester = getDirectoryTester();
        try {
            directoryTester.assertDNIsA(dn, objectclass);

        } finally {
            directoryTester.disconnect();
        }
    }

    /**
     * Assert that the entry identified by {@code dn} has an attribute named {@code attributeName}.
     *
     * @param dn            The distinguished name
     * @param attributeName The attribute name
     * @since 1.0.2
     */
    public void assertDNHasAttribute(final String dn,
                                     final String attributeName) {
        final DirectoryTester directoryTester = getDirectoryTester();
        try {
            directoryTester.assertDNHasAttribute(dn, attributeName);

        } finally {
            directoryTester.disconnect();
        }
    }

    /**
     * Assert that the entry identified by {@code dn} has an attribute named {@code attributeName} with
     * the attribute value(s) {@code attributeName}.
     *
     * @param dn             The distinguished name
     * @param attributeName  The attribute name
     * @param attributeValue The attribute value(s)
     * @since 1.0.2
     */
    public void assertDNHasAttributeValue(final String dn,
                                          final String attributeName,
                                          final String... attributeValue) {
        final DirectoryTester directoryTester = getDirectoryTester();
        try {
            directoryTester.assertDNHasAttributeValue(dn, attributeName, attributeValue);

        } finally {
            directoryTester.disconnect();
        }
    }

    /**
     * Create a {@link DirectoryTester} that connects to the in-memory LDAP directory server created by this rule.
     *
     * @return The {@link DirectoryTester}.
     * @since 1.0.2
     */
    private DirectoryTester getDirectoryTester() {
        return new DirectoryTester("localhost", annotation.port(), annotation.authDN(), annotation.authPassword());
    }

}
