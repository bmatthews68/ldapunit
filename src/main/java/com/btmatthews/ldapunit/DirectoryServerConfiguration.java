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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to specify the parameters used to configure the LDAP directory server. The
 * annotation can be applied to the test class or method. If the annotation has been applied to both
 * the test class and the test method the the annotation on the test method is used.
 *
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 1.0.0
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DirectoryServerConfiguration {

    /**
     * The TCP port that the LDAP directory server will be configured to listen on.
     *
     * @return The TCP port.
     */
    int port() default 10389;

    /**
     * The DN that will be configured as the root of the LDAP directory.
     *
     * @return The DN.
     */
    String baseDN() default "dc=btmatthews,dc=com";

    /**
     * The DN that will be configured as the administrator account identifier.
     *
     * @return The DN.
     */
    String authDN() default "uid=admin,ou=system";

    /**
     * The password that will be configured as the authentication credentials for the administrator account.
     *
     * @return The password.
     */
    String authPassword() default "secret";

    /**
     * The location of the an optional LDIF file that can be used to seed the LDAP directory with an initial data set.
     * The file may be located on the file system or the classpath. The classpath is checked
     * first and then falls back to the file system if it was not found on the class path.
     *
     * @return The LDIF file path.
     */
    @Deprecated String ldifFile() default "";

    /**
     * The locations of optional LDIF files that can be used to see the LDAP directory with an initial data set.
     * The files may localed on the file system or the classpath. The classpath is checked first and then falls back
     * to che file system if it was not found on the class path.
     *
     * @return An array of LDIF file paths.
     */
    String[] ldifFiles() default {};
    
    /**
     * The location of the optional schema LDIF file that can be use to have a LDAP directory with a custom schema.
     * The file may located on the file system or the classpath. The classpath is checked first and then falls back
     * to the file system if it was no found on the class path.
     * 
     * @return The file path of the schema in LDIF.
     */
    String ldifSchema() default "";
    
}
