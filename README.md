# Symphony Third-Party Authentication

This Third-Party Authentication library allows for authentication of apps in the Symphony platform and authentication of Symphony users in third-party applications.
The library is split into two parts, **Application Authentication Filter** and 
**Application Authentication API**. Both will be explained below:

## Application Authentication Filter 

#### Purpose
Enables third-party applications to perform the authentication process, the purpose of which is to allow the application to determine the user's identity within Symphony.

#### Workflow
When the application receives an HTTP request, the filter gets the JSON Web Token from the 'Authorization' HTTP header and executes the validation process before retrieving the user information. The validation process consists of the following steps:

1. Check the algorithm used to sign the JWT. Currently, Symphony supports only the RS512 algorithm. Any others will be rejected.
2. Check the JWT signature and expiration date using the POD public certificate as the signing key. The POD public certificate is retrieved through the API call to the POD API endpoint: ```<<POD_URL>>/pod/v1/podcert```

To improve performance, the library stores the POD public certificate in a local cache (in-memory). This approach will reduce unnecessary requests to the POD API. The library will also store the JWT payload in a local cache to avoid executing the above second step repeatedly for the same token.

This filter requires the class implementation to get POD and Session Auth base URL's. It must be provided by the library consumer.

#### How to use
This section describes the steps by which the third-party application should use this library.

1. Declare symphony-app-authentication-filter as a dependency in the POM file
2. Declare SLF4J implementation as a dependency (for example: Log4j, Logback, etc)
3. Implement the ServicesInfoProvider interface and register this as the default provider in the ServicesInfoProviderFactory
4. Register 'org.symphonyoss.integration.application.authentication.jwt.JwtFilter' as a web filter using web.xml descriptor for traditional Java webapps. For Spring boot applications, follow [this guide](https://docs.spring.io/spring-boot/docs/1.5.9.RELEASE/reference/htmlsingle/#boot-features-embedded-container-servlets-filters-listeners-beans). 

The ServicesInfoProvider implementation should retrieve the POD and Session auth base URL's

#### Spring Boot Starter
To simplify the usage of this library we provide the 'symphony-app-authentication-filter-starter' to enable Spring Boot autoconfiguration. Third-party applications that have already been implemented using Spring Boot just need to declare this starter as a dependency.

The library consumer doesn't need to register the ServicesInfoProvider implementation in the factory and also doesn't need to register the web filter - the autoconfiguration provided by the library does it for you. You must provide a default implementation for the ServicesInfoProvider interface that enables the library consumer to describe the POD and Session Auth base URL's in the YAML configuration file.

###### Example:

```yaml
app-authentication:
  enabled: true
  pod:
    host: ${pod.host}
    port: ${pod.port:443}
  session_auth:
    host: ${session_auth.host}
    port: ${session_auth.port:443}
  filter:
    url-patterns:
      - "/v1/*"
```

## Application Authentication API
_**TO DO**_
