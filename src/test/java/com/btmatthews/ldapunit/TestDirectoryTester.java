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
 * Created with IntelliJ IDEA.
 * User: Brian
 * Date: 15/01/13
 * Time: 12:29
 * To change this template use File | Settings | File Templates.
 */
public class TestDirectoryTester {

    @Rule
    public DirectoryServerRule directoryServerRule = new DirectoryServerRule();
    private DirectoryTester tester;

    @Before
    public void setUp() {
        tester = new DirectoryTester("localhost", 10389, "uid=admin,ou=system", "secret");
    }

    @After
    public void tearDown() {
        tester.disconnect();
    }

    @Test
    @DirectoryServerConfiguration
    public void checkVerifyDNExists() {
        assertTrue(tester.verifyDNExists("dc=btmatthews,dc=com"));
        assertFalse(tester.verifyDNExists("ou=People,dc=btmatthews,dc=com"));
    }

    @Test
    @DirectoryServerConfiguration
    public void checkAssertDNExistsSucceeds() {
        tester.assertDNExists("dc=btmatthews,dc=com");
    }

    @Test(expected = AssertionError.class)
    @DirectoryServerConfiguration
    public void checkAssertDNExistsFails() {
        tester.assertDNExists("ou=People,dc=btmatthews,dc=com");
    }

    @Test(expected = DirectoryTesterException.class)
    @DirectoryServerConfiguration
    public void throwsExceptionIfInvalidDN() {
        tester.verifyDNExists("dc:btmatthews,dc:com");
    }
}
