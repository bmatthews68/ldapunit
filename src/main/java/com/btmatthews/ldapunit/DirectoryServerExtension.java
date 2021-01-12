package com.btmatthews.ldapunit;

import com.unboundid.ldap.listener.InMemoryDirectoryServer;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldif.LDIFException;
import org.junit.jupiter.api.extension.*;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;

import java.io.IOException;

/**
 * @since 2.0.0
 */
public class DirectoryServerExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback, ParameterResolver {

    private static final String SERVER = "server";

    @Override
    public void beforeTestExecution(final ExtensionContext extensionContext) {
        final DirectoryServerConfiguration annotation = getAnnotation(extensionContext);
        if (annotation != null) {
            final InMemoryDirectoryServer server = startServer(annotation);
            getStore(extensionContext).put(SERVER, server);
        }
    }

    @Override
    public void afterTestExecution(final ExtensionContext extensionContext) {
        final Store store = getStore(extensionContext);
        final InMemoryDirectoryServer server = store.get(SERVER, InMemoryDirectoryServer.class);
        if (server != null) {
            DirectoryServerUtils.stopServer(server);
        }
    }

    private Store getStore(final ExtensionContext extensionContext) {
        final Namespace namespace = Namespace.create(DirectoryServerExtension.class, extensionContext.getRequiredTestMethod());
        return extensionContext.getStore(namespace);
    }

    private DirectoryServerConfiguration getAnnotation(final ExtensionContext extensionContext) {
        final DirectoryServerConfiguration annotation = extensionContext.getRequiredTestMethod().getAnnotation(DirectoryServerConfiguration.class);
        if (annotation == null) {
            return extensionContext.getRequiredTestClass().getAnnotation(DirectoryServerConfiguration.class);
        } else {
            return annotation;
        }
    }

    private InMemoryDirectoryServer startServer(final DirectoryServerConfiguration annotation) {
        try {
            return DirectoryServerUtils.startServer(annotation.port(), annotation.baseDN(), annotation.authDN(), annotation.authPassword(), annotation.ldifFiles());
        } catch (final LDIFException | LDAPException | IOException e) {
            throw new AssertionError("Failed to launch embedded Directory Server", e);
        }
    }

    @Override
    public boolean supportsParameter(final ParameterContext parameterContext,
                                     final ExtensionContext extensionContext)
            throws ParameterResolutionException {
        return DirectoryTester.class.equals(parameterContext.getParameter().getType());
    }

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
