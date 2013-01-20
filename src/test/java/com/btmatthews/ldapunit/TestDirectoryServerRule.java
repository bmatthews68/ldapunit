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
 * Unit test the {@link DirectoryServerRule} rule.
 *
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
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
        final DirectoryTester tester = new DirectoryTester("localhost", 10389, "uid=admin,ou=system", "secret");
        try {
            tester.assertDNExists("dc=btmatthews,dc=com");
        } finally {
            tester.disconnect();
        }
    }
}
