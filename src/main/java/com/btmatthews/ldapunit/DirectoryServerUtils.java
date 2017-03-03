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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

import com.unboundid.ldap.listener.InMemoryDirectoryServer;
import com.unboundid.ldap.listener.InMemoryDirectoryServerConfig;
import com.unboundid.ldap.listener.InMemoryListenerConfig;
import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.DN;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.schema.Schema;
import com.unboundid.ldif.LDIFChangeRecord;
import com.unboundid.ldif.LDIFException;
import com.unboundid.ldif.LDIFReader;

/**
 * Helper functions to start the in-memory LDAP directory server, load LDAP directory entries from an LDIF files and
 * shut down the in-memory LDAP directory server.
 *
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 1.0.1
 */
final class DirectoryServerUtils {

    /**
     * Create and configure an embedded LDAP directory server, load seed data and start the server.
     *
     * @param ldifFile The LDIF resource or file from which LDIF records will be loaded.
     * @return The {@link com.unboundid.ldap.listener.InMemoryDirectoryServer} object.
     * @throws com.unboundid.ldif.LDIFException     If there was an error in the LDIF data.
     * @throws com.unboundid.ldap.sdk.LDAPException If there was a problem configuring or starting the embedded LDAP directory server.
     * @throws java.io.IOException                  If there was a problem reading the LDIF data.
     */
    @Deprecated
    static InMemoryDirectoryServer startServer(final int port,
                                               final String baseDN,
                                               final String authDN,
                                               final String authPassword,
                                               final String ldifFile,
                                               final String ldifSchema)
            throws LDIFException, LDAPException, IOException {
        return startServer(port, baseDN, authDN, authPassword, new String[]{ldifFile}, ldifSchema);
    }

    /**
     * Create and configure an embedded LDAP directory server, load seed data and start the server.
     *
     * @param ldifFiles The LDIF resources or files from which LDIF records will be loaded.
     * @return The {@link com.unboundid.ldap.listener.InMemoryDirectoryServer} object.
     * @throws com.unboundid.ldif.LDIFException     If there was an error in the LDIF data.
     * @throws com.unboundid.ldap.sdk.LDAPException If there was a problem configuring or starting the embedded LDAP directory server.
     * @throws java.io.IOException                  If there was a problem reading the LDIF data.
     */
    static InMemoryDirectoryServer startServer(final int port,
                                               final String baseDN,
                                               final String authDN,
                                               final String authPassword,
                                               final String[] ldifFiles,
                                               final String ldifSchema)
            throws LDIFException, LDAPException, IOException {
        final InMemoryListenerConfig listenerConfig = InMemoryListenerConfig.createLDAPConfig("default", port);
        final InMemoryDirectoryServerConfig config = new InMemoryDirectoryServerConfig(new DN(baseDN));
        loadSchema(config, ldifSchema);
        config.setListenerConfigs(listenerConfig);
        config.addAdditionalBindCredentials(authDN, authPassword);
        final InMemoryDirectoryServer server = new InMemoryDirectoryServer(config);
        server.add(new Entry(baseDN, new Attribute("objectclass", "domain", "top")));
        server.startListening();
        for (final String ldifFile : ldifFiles) {
            loadData(server, ldifFile);
        }
        return server;
    }

    /**
     * Load LDIF records from a file to seed the LDAP directory.
     *
     * @param server   The embedded LDAP directory server.
     * @param ldifFile The LDIF resource or file from which LDIF records will be loaded.
     * @throws com.unboundid.ldif.LDIFException     If there was an error in the LDIF data.
     * @throws com.unboundid.ldap.sdk.LDAPException If there was a problem loading the LDIF records into the LDAP directory.
     * @throws java.io.IOException                  If there was a problem reading the LDIF records from the file.
     */
    private static void loadData(final InMemoryDirectoryServer server,
                                 final String ldifFile)
            throws LDIFException, LDAPException, IOException {
        if (ldifFile != null && ldifFile.length() > 0) {
            final InputStream inputStream;
            final File file = new File(ldifFile);
            if (file.exists()) {
                inputStream = new FileInputStream(file);
            } else {
                final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                inputStream = classLoader.getResourceAsStream(ldifFile);
            }
            if (inputStream != null) {
                loadData(server.getConnection(), inputStream);
            }
        }
    }

    private static void loadData(final LDAPConnection connection,
                                 final InputStream inputStream)
            throws LDIFException, LDAPException, IOException {
        try {
            final LDIFReader reader = new LDIFReader(inputStream);
            LDIFChangeRecord changeRecord = reader.readChangeRecord(true);
            while (changeRecord != null) {
                changeRecord.processChange(connection);
                changeRecord = reader.readChangeRecord(true);
            }
            reader.close();
        } finally {
            inputStream.close();
        }
    }
    
    /**
     * Load schema from a file to set in the LDAP directory.
     * 
     * @param config The configuration of the memory directory server.
     * @param ldifSchema The LDIF schema resource name or file path.
     * @throws LDIFException	If there was an error in the LDIF schema.	
     * @throws IOException		If there was a problem reading the LDIF schema from the file.
     */
    private static void loadSchema(final InMemoryDirectoryServerConfig config,
    							  final String ldifSchema)
    	    throws LDIFException, IOException {
		if ( ldifSchema != null && !ldifSchema.isEmpty() ) {
			Schema schema = null;
			final File file = new File(ldifSchema);
			final String path = getPathFromResourceName(ldifSchema);
			
			if (path != null) {
				schema = Schema.getSchema(path);
			}
			else if (file.exists()) {
				schema = Schema.getSchema(file);
			}
			
			if (schema != null) {
				config.setSchema(schema);
			}
		}
	}
    
    /**
     * @param name The resource name.
     * @return The filesystem path of the resource file, null if not exists.
     */
    private static String getPathFromResourceName(String name) {
    	try {
			URL url = ClassLoader.getSystemResource(name);
			if ( url != null ) {
				return Paths.get( url.toURI() ).toString();
			}
		} catch (URISyntaxException e) {
			// Nothing
		}
    	return null;
    }

    /**
     * Shutdown the embedded LDAP directory server.
     *
     * @param server The embedded LDAP directory server.
     */
    static void stopServer(final InMemoryDirectoryServer server) {
        server.shutDown(true);
    }
}