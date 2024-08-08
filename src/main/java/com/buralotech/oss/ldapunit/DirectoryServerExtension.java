/*
 * Copyright 2021-2024 Brian Thomas Matthews
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

import com.unboundid.ldap.listener.InMemoryDirectoryServer;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldif.LDIFException;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.io.IOException;

/**
 * JUnit 5 (Jupiter) extension that will start an embedded directory server before the test method execution and
 * stop the embedded directory server when the test method completes.
 *
 * @author <a href="mailto:bmatthews68@gmail.com">Brian Matthews</a>
 * @since 2.0.0
 */
public class DirectoryServerExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback,
        ParameterResolver {

    /**
     * The name of the property used to cache the reference to the embedded directory server.
     */
    private static final String SERVER = "server";

    /**
     * This callback is invoked before the test method is executed and is responsible for starting the embedded
     * directory server.
     *
     * @param extensionContext – the extension context for the Executable about to be invoked; never {@code null}.
     */
    @Override
    public void beforeTestExecution(final ExtensionContext extensionContext) {
        final DirectoryServerConfiguration annotation = getAnnotation(extensionContext);
        if (annotation != null) {
            try {
                final InMemoryDirectoryServer server = DirectoryServerUtils.startServer(annotation);
                getStore(extensionContext).put(SERVER, server);
            } catch (final LDIFException | LDAPException | IOException e) {
                throw new AssertionError("Failed to launch embedded Directory Server", e);
            }
        }
    }

    /**
     * This callback is invoked after the test method is executed and is responsible for stopping the embedded
     * directory server.
     *
     * @param extensionContext – the extension context for the Executable about to be invoked; never {@code null}.
     */
    @Override
    public void afterTestExecution(final ExtensionContext extensionContext) {
        final Store store = getStore(extensionContext);
        final InMemoryDirectoryServer server = store.get(SERVER, InMemoryDirectoryServer.class);
        if (server != null) {
            DirectoryServerUtils.stopServer(server);
        }
    }

    /**
     * Get teh context storage for the method invocation.
     *
     * @param extensionContext – the extension context for the Executable about to be invoked; never {@code null}.
     * @return The context store.
     */
    private Store getStore(final ExtensionContext extensionContext) {
        final Namespace namespace = Namespace.create(
                DirectoryServerExtension.class,
                extensionContext.getRequiredTestMethod());
        return extensionContext.getStore(namespace);
    }

    /**
     * Locate the annotation that specifies the configuration for the embedded directory server. The annotation is
     * sought on the test method declaration before falling back to check the test class.
     *
     * @param extensionContext – the extension context for the Executable about to be invoked; never {@code null}.
     * @return The {@code DirectoryServerConfiguration} annotation if found. Otherwise, {@code null}.
     */
    private DirectoryServerConfiguration getAnnotation(final ExtensionContext extensionContext) {
        final DirectoryServerConfiguration annotation = extensionContext.getRequiredTestMethod()
                .getAnnotation(DirectoryServerConfiguration.class);
        if (annotation == null) {
            return extensionContext.getRequiredTestClass().getAnnotation(DirectoryServerConfiguration.class);
        } else {
            return annotation;
        }
    }

    /**
     * Check the parameter type is {@link DirectoryTester}.
     *
     * @param parameterContext The context for the parameter for which an argument should be resolved;
     *                         never {@code null}.
     * @param extensionContext The extension context for the Executable about to be invoked; never {@code null}.
     * @return {@code true} if the parameter type is {@link DirectoryTester}. Otherwise, {@code false}.
     */
    @Override
    public boolean supportsParameter(final ParameterContext parameterContext,
                                     final ExtensionContext extensionContext)
            throws ParameterResolutionException {
        return DirectoryTester.class.equals(parameterContext.getParameter().getType());
    }

    /**
     * Resolve {@link DirectoryTester} parameters.
     *
     * @param parameterContext The context for the parameter for which an argument should be resolved;
     *                         never {@code null}.
     * @param extensionContext –The extension context for the Executable about to be invoked; never {@code null}.
     * @return The resolved parameter.
     * @throws ParameterResolutionException If a connection to the directory server could not be established.
     */
    @Override
    public Object resolveParameter(final ParameterContext parameterContext,
                                   final ExtensionContext extensionContext)
            throws ParameterResolutionException {
        final InMemoryDirectoryServer server = getStore(extensionContext).get(SERVER, InMemoryDirectoryServer.class);
        if (server != null) {
            try {
                return new DirectoryTester(server.getConnection());
            } catch (final LDAPException e) {
                throw new ParameterResolutionException("Cannot connect to directory server", e);
            }
        }
        return null;
    }
}
