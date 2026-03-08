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

import org.assertj.core.api.AbstractAssert;

/**
 * Assertions for testing existence of entries and attributes in LDAP directory by wrapping a {@link DirectoryTester}.
 */
public class DirectoryAssertions extends AbstractAssert<DirectoryAssertions, DirectoryTester> {

    /**
     * Initialise with a wrapped {@link DirectoryTester}.
     *
     * @param tester The wrapped {@link DirectoryTester}.
     */
    DirectoryAssertions(final DirectoryTester tester) {
        super(tester, DirectoryAssertions.class);
    }

    /**
     * Assert that an entry with the specified distinguished name exists in the LDAP directory.
     *
     * @param dn The distinguished name.
     * @return Always returns {@code this}.
     */
    public DirectoryAssertions exists(final String dn) {
        actual.assertDNExists(dn);
        return this;
    }

    /**
     * Assert that an entry with the specified distinguished name in the LDAP directory has the required object class.
     *
     * @param dn          The distinguished name.
     * @param objectclass The required object class.
     * @return Always returns {@code this}.
     */
    public DirectoryAssertions isA(final String dn,
                                   final String objectclass) {
        actual.assertDNIsA(dn, objectclass);
        return this;
    }

    /**
     * Assert that an entry with the specified distinguished name in the LDAP directory has a required attribute.
     *
     * @param dn            The distinguished name.
     * @param attributeName The name of the required attribute.
     * @return Always returns {@code this}.
     */
    public DirectoryAssertions hasAttribute(final String dn,
                                            final String attributeName) {
        actual.assertDNHasAttribute(dn, attributeName);
        return this;
    }

    /**
     * Assert that an entry with the specified distinguished name in the LDAP directory has a required attribute with
     * the specified value(s).
     *
     * @param dn             The distinguished name.
     * @param attributeName  The name of the required attribute.
     * @param attributeValue The attribute value(s).
     * @return Always returns {@code this}.
     */
    public DirectoryAssertions hasAttributeValue(final String dn,
                                                 final String attributeName,
                                                 final String... attributeValue) {
        actual.assertDNHasAttributeValue(dn, attributeName, attributeValue);
        return this;
    }
}
