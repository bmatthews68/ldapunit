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
     * Modifies the method-running {@link Statement} to start implement this test-running rule. The configuration for
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
        DirectoryServerConfiguration annotation = description.getAnnotation(DirectoryServerConfiguration.class);
        if (annotation == null) {
            final Class testClass = description.getTestClass();
            annotation = (DirectoryServerConfiguration) testClass.getAnnotation(DirectoryServerConfiguration.class);
        }
        if (annotation != null) {
            return new DirectoryServerStatement(base, annotation);
        }
        return base;
    }
}
