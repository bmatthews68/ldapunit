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

/**
 * Runtime exception raised by {@link DirectoryTester} when there is a problem communicating with the LDAP directory
 * server.
 *
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 1.0.0
 */
public final class DirectoryTesterException extends RuntimeException {

    /**
     * The default constructor.
     */
    public DirectoryTesterException() {
    }

    /**
     * Construct a {@link DirectoryTesterException} with an message describing the source of the exception.
     *
     * @param message Describes the source of the exception.
     */
    public DirectoryTesterException(final String message) {
        super(message);
    }

    /**
     * Construct a {@link DirectoryTesterException} with a root cause.
     *
     * @param cause The root cause of the exception.
     */
    public DirectoryTesterException(final Throwable cause) {
        super(cause);
    }

    /**
     * Construct a {@link DirectoryTesterException} with an message describing the source of the exception and
     * a root cause.
     *
     * @param message Describes the source of the exception.
     * @param cause   The root cause of the exception.
     */
    public DirectoryTesterException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
