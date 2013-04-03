LDAPUnit
========

**LDAPUnit** is a Java library help create unit tests for that depend on a LDAP directory server.

DirectoryServerRule
-------------------

[DirectoryServerRule](http://ldapunit.btmatthews.com/apidocs/com/btmatthews/ldapunit/DirectoryServerRule.html) is used
launch an embedded LDAP directory server so that the unit tests do not have to be dependent on an external LDAP
directory server with unpredictable state.

To use the
[DirectoryServerRule](http://ldapunit.btmatthews.com/apidocs/com/btmatthews/ldapunit/DirectoryServerRule.html) you need
to instantiate the rule as a public member of the test class and annotate it with
[Rule](http://kentbeck.github.com/junit/javadoc/4.10/org/junit/Rule.html). The rule will have no effect unless the class
or methods have been annotated with
[DirectoryServerConfiguration](http://ldapunit.btmatthews.com/apidocs/com/btmatthews/ldapunit/DirectoryServerConfiguration.html).

```java
import com.btmatthews.ldapunit.DirectoryServerConfiguration;
import com.btmatthews.ldapunit.DirectoryServerRule;
import org.junit.Rule;
import org.junit.Test;

@DirectoryServerConfiguration
public class Test {

    @Rule
    public DirectoryServerRule directoryServerRule = new DirectoryServerRule();

    @Test
    public void testSomething() {
        // Unit test code that depends on LDAP directory server
    }
}
```

The
[DirectoryServerConfiguration](http://ldapunit.btmatthews.com/apidocs/com/btmatthews/ldapunit/DirectoryServerConfiguration.html)
annotation is used to provide the configuration parameters for the embedded LDAP directory server. The following
parameters can be configured:

<table>
<thead><tr>
<th>Name</th>
<th>Description</th>
<th>Default</th>
</tr></thead>
<tbody>
<tr>
<td>port</td>
<td>The TCP port that the LDAP directory server will be configured to listen on.</td>
<td>10389</td>
</tr>
<tr>
<td>baseDN</td>
<td>The DN that will be configured as the root of the LDAP directory.</td>
<td>dc=btmatthews,dc=com</td>
</tr>
<tr>
<td>authDN</td>
<td>The DN that will be configured as the administrator account identifier.</td>
<td>uid=admin,ou=system</td>
</tr>
<tr>
<td>password</td>
<td>The password that will be configured as the authentication credentials for the administrator account.</td>
<td>secret</td>
</tr>
<tr>
<td>ldifFile</td>
<td>The location of the an optional LDIF file that can be used to seed the LDAP directory with an initial data set. The
file may be located on the file system or the classpath. The classpath is checked first and then falls back to the file
system if it was not found on the class path.</td>
<td></td>
</tbody>
</table>

The following methods can be used to make assertions about or verify the contents of the LDAP directory:

* **assertDNExists(String dn)** - asserts that an entry exists in the LDAP directory with the distinguished name of
  **dn**.

* **verifyDNExists(String dn)** - tests to see if an entry exists in the LDAP directory with the distinguished name
  of **dn**.

* **assertDNIsA(String dn, String objectclass)** - asserts that an entry exists in the LDAP directory with the
  distinguished name of **dn** and that it is of type **objectclass**.

* **verifyDNIsA(String dn, String objectclass)** - tests to see if an entry exists in the LDAP directory with the
  distinguished name of **dn** and check that it is of type **objectclass**.

* **assertDNHasAttribute(String dn, String attributeName)** - asserts that an entry exists in the LDAP directory
  with the distinguished name of **dn** and that it has an attribute named **attributeName**.

* **verifyDNHasAttribute(String dn, String attributeName)** - tests to see if an entry exists in the LDAP directory
    with the distinguished name of **dn** and check that it has an attribute named **attributeName**.

* **assertDNHasAttributeValue(String dn, String attributeName, String... attributeValue)** - assert that an entry
  exists in the LDAP directory with the distinguished nane of **dn** and that it has an attribute named
  **attributeName** with value(s) **attributeValues**.

* <<verifyDNHasAttributeValue(String dn, String attributeName, String... attributeValue)** - tests to see if an entry
  exists in the LDAP directory with the distinguished name of **dn** and check that it has an attribute named
  **attributeName** with value(s) **attributeValues**.

DirectoryTester
---------------

[DirectoryTester](http://ldapunit.btmatthews.com/apidocs/com/btmatthews/ldapunit/DirectoryTester.html) is used to
connect to an LDAP directory server (embedded or external) and make assertions about or verify the contents of the LDAP
directory.

```java
import com.btmatthews.ldapunit.DirectoryTester;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class Test {

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
  public void testSomething() {
     // Do something that affects the LDAP directory
     // Check outcomes with tester.assertXXX() and tester.verifyXXX() methods
  }
}
```

There are two variants of the
[DirectoryTester](http://ldapunit.btmatthews.com/apidocs/com/btmatthews/ldapunit/DirectoryTester.html) constructor

* **DirectoryTester(String hostname, int port)** - connects anonymously to the directory server on the host with
  name or IP address specified by **hostname** that is listening on the TCP port specified by **port**.

* **DirectoryTester(String hostname, int port, String bindDN, String password)** - connects to the directory server
  on the host with name or IP address specified by **hostname** that is listening on the TCP port specified by
  **port**. Then it binds to the using the authentication identifier and credentials specified by **bindDN** and
  **password**.

The following methods can be used to make assertions about or verify the contents of the LDAP directory:

* **assertDNExists(String dn)** - asserts that an entry exists in the LDAP directory with the distinguished name of
  **dn**.

* **verifyDNExists(String dn)** - tests to see if an entry exists in the LDAP directory with the distinguished name
  of **dn**.

* **assertDNIsA(String dn, String objectclass)** - asserts that an entry exists in the LDAP directory with the
  distinguished name of **dn** and that it is of type **objectclass**.

* **verifyDNIsA(String dn, String objectclass)** - tests to see if an entry exists in the LDAP directory with the
  distinguished name of **dn** and check that it is of type **objectclass**.

* **assertDNHasAttribute(String dn, String attributeName)** - asserts that an entry exists in the LDAP directory
  with the distinguished name of **dn** and that it has an attribute named **attributeName**.

* **verifyDNHasAttribute(String dn, String attributeName)** - tests to see if an entry exists in the LDAP directory
    with the distinguished name of **dn** and check that it has an attribute named **attributeName**.

* **assertDNHasAttributeValue(String dn, String attributeName, String... attributeValue)** - assert that an entry
  exists in the LDAP directory with the distinguished nane of **dn** and that it has an attribute named
  **attributeName** with value(s) **attributeValues**.

* <<verifyDNHasAttributeValue(String dn, String attributeName, String... attributeValue)** - tests to see if an entry
  exists in the LDAP directory with the distinguished name of **dn** and check that it has an attribute named
  **attributeName** with value(s) **attributeValues**.

The connection should be closed by calling **disconnect()**.

Maven Central Coordinates
-------------------------
**LDAPUnit** has been published in [Maven Central](http://search.maven.org) at the following
coordinates:

```xml
<plugin>
    <groupId>com.btmatthews.ldapunit</groupId>
    <artifactId>ldapunit</artifactId>
    <version>1.0.0</version>
</plugin>
```

Credits
-------
The approach for implementing the **LDAPUnit**'s **DirectoryServerRule** is based heavily on the **OpenDJRule**
implemented in the https://github.com/ehsavoie/embedded-ldap project.

Internally **LDAPUnit** is using the [UnboundID LDAP SDK](https://www.unboundid.com/products/ldap-sdk) to run the
embedded LDAP directory server and as the API for communicating with an LDAP directory server.

License & Source Code
---------------------
The **LDAPUnit** is made available under the
[Apache License](http://www.apache.org/licenses/LICENSE-2.0.html) and the source code is hosted on
[GitHub](http://github.com) at https://github.com/bmatthews68/ldapunit.
