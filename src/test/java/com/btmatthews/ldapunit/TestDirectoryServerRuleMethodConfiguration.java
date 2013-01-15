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

import org.junit.Rule;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: Brian
 * Date: 15/01/13
 * Time: 11:33
 * To change this template use File | Settings | File Templates.
 */
public class TestDirectoryServerRuleMethodConfiguration {

    @Rule
    public DirectoryServerRule directoryServerRule = new DirectoryServerRule();

    @DirectoryServerConfiguration
    @Test
    public void checkServerIsRunning() {
        final DirectoryTester tester = new DirectoryTester("localhost", 10389, "uid=admin,ou=system", "secret");
        tester.disconnect();
    }

    @Test(expected = DirectoryTesterException.class)
    public void checkServerIsNotRunning() {
        new DirectoryTester("localhost", 10389, "uid=admin,ou=system", "secret");
    }

    @Test
    @DirectoryServerConfiguration(ldifFile = "com/btmatthews/ldapunit/initial.ldif")
    public void canLoadFromClasspath() {
        final DirectoryTester tester = new DirectoryTester("localhost", 10389, "uid=admin,ou=system", "secret");
        try {
            tester.assertDNExists("dc=btmatthews,dc=com");
            tester.assertDNExists("ou=People,dc=btmatthews,dc=com");
            tester.assertDNExists("cn=Bart Simpson,ou=People,dc=btmatthews,dc=com");
            tester.assertDNExists("uid=lsimpson,ou=People,dc=btmatthews,dc=com");
            tester.assertDNExists("uid=hsimpson,ou=People,dc=btmatthews,dc=com");
            tester.assertDNExists("uid=msimpson,ou=People,dc=btmatthews,dc=com");
            tester.assertDNExists("uid=ccarlson,ou=People,dc=btmatthews,dc=com");
        } finally {
            tester.disconnect();
        }
    }

    @Test
    @DirectoryServerConfiguration(ldifFile = "src/test/resources/com/btmatthews/ldapunit/initial.ldif")
    public void canLoadFromFilesystem() {
        final DirectoryTester tester = new DirectoryTester("localhost", 10389, "uid=admin,ou=system", "secret");
        try {
            tester.assertDNExists("dc=btmatthews,dc=com");
            tester.assertDNExists("ou=People,dc=btmatthews,dc=com");
            tester.assertDNExists("cn=Bart Simpson,ou=People,dc=btmatthews,dc=com");
            tester.assertDNExists("uid=lsimpson,ou=People,dc=btmatthews,dc=com");
            tester.assertDNExists("uid=hsimpson,ou=People,dc=btmatthews,dc=com");
            tester.assertDNExists("uid=msimpson,ou=People,dc=btmatthews,dc=com");
            tester.assertDNExists("uid=ccarlson,ou=People,dc=btmatthews,dc=com");
        } finally {
            tester.disconnect();
        }
    }
}
