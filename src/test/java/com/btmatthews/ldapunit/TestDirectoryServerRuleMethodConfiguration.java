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

import org.junit.Rule;
import org.junit.Test;

/**
 * Unit test the {@link DirectoryServerRule} rule when the methods are annotated
 * with {@link DirectoryServerConfiguration}.
 *
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 1.0.0
 */
public class TestDirectoryServerRuleMethodConfiguration {

    /**
     * The rule being tested.
     */
    @Rule
    public DirectoryServerRule directoryServerRule = new DirectoryServerRule();

    /**
     * Verify that the rule starts a server when the test method is annotated with
     * {@link DirectoryServerConfiguration}.
     */
    @DirectoryServerConfiguration
    @Test
    public void checkServerIsRunning() {
        final DirectoryTester tester = new DirectoryTester();
        try {
            tester.assertDNExists("dc=btmatthews,dc=com");
        } finally {
            tester.disconnect();
        }
    }

    /**
     * Verify that the rule does not start server when the test method is not annotated with
     * {@link DirectoryServerConfiguration}.
     */
    @Test(expected = DirectoryTesterException.class)
    public void checkServerIsNotRunning() {
        new DirectoryTester();
    }

    /**
     * Verify the that the rule will seed the server with LDAP directory entries when the {@code ldifFile}
     * parameter of the annotation is specified. This variation loads the LDIF file as a classpath resource.
     */
    @Test
    @DirectoryServerConfiguration(ldifFiles = "com/btmatthews/ldapunit/initial.ldif")
    public void canLoadFromClasspath() {
        directoryServerRule.assertDNExists("dc=btmatthews,dc=com");
        directoryServerRule.assertDNExists("ou=People,dc=btmatthews,dc=com");
        directoryServerRule.assertDNExists("cn=Bart Simpson,ou=People,dc=btmatthews,dc=com");
        directoryServerRule.assertDNExists("uid=lsimpson,ou=People,dc=btmatthews,dc=com");
        directoryServerRule.assertDNExists("uid=hsimpson,ou=People,dc=btmatthews,dc=com");
        directoryServerRule.assertDNExists("uid=msimpson,ou=People,dc=btmatthews,dc=com");
        directoryServerRule.assertDNExists("uid=ccarlson,ou=People,dc=btmatthews,dc=com");

    }

    /**
     * Verify the that the rule will seed the server with LDAP directory entries when the {@code ldifFile}
     * parameter of the annotation is specified. This variation loads the LDIF file using a file system path.
     */
    @Test
    @DirectoryServerConfiguration(ldifFiles = "src/test/resources/com/btmatthews/ldapunit/initial.ldif")
    public void canLoadFromFilesystem() {
        directoryServerRule.assertDNExists("dc=btmatthews,dc=com");
        directoryServerRule.assertDNExists("ou=People,dc=btmatthews,dc=com");
        directoryServerRule.assertDNExists("cn=Bart Simpson,ou=People,dc=btmatthews,dc=com");
        directoryServerRule.assertDNExists("uid=lsimpson,ou=People,dc=btmatthews,dc=com");
        directoryServerRule.assertDNExists("uid=hsimpson,ou=People,dc=btmatthews,dc=com");
        directoryServerRule.assertDNExists("uid=msimpson,ou=People,dc=btmatthews,dc=com");
        directoryServerRule.assertDNExists("uid=ccarlson,ou=People,dc=btmatthews,dc=com");
    }
}
