# Symphony Third-Party Authentication

This Third-Party Authentication suits to authenticate apps in Symphony platform and authenticate Symphony users in thirdy-part applications.
The authentication is splited in two parts, **Application Authentication Filter** and 
**Application Authentication API** and both will be explained below:

## Application Authentication Filter 

#### Motivation
Enable third-party applications to perform authentication process using Symphony cloud-infrastructure. This approach allows the third-party application to check the user identity within the Symphony platform.

#### Workflow
When the application receives an HTTP request, the filter must get the JSON Web Token from the 'Authorization' HTTP header and executes the validation process before retrieve the user information. The validation process consist of the following steps:

1. Check the algorithm used to sign the JWT. Currently, Symphony supports only RS512 algorithm, other than that should be rejected.
2. Check the JWT signature and expiration date using POD public certificate as signing key. POD public certificate should be retrieved through the API call to POD API endpoint: ```<<POD_URL>>/pod/v1/podcert```

To improve performance, the library should store the POD public certificate in a local cache (in-memory). This approach will reduce a lot of unnecessary requests to the POD API.

Other than that, the library should store JWT payload in a local cache as well to avoid executing the step 2 all the time for the same token.

This filter requires the class implementation to get POD and Session Auth base URL's. It must be provided by the library consumer.

#### How to use
This section describes the steps how to the third-party application should use this library.

1. Declare symphony-app-authentication-filter as dependency in POM file
2. Declare SLF4J implementation as dependency (for example: Log4j, Logback, etc)
3. Implements ServicesInfoProvider interface and register this as default provider in the ServicesInfoProviderFactory
4. Register 'org.symphonyoss.integration.application.authentication.jwt.JwtFilter' as a web filter using web.xml descriptor for traditional Java webapps, for Spring boot applications follow [this guide](https://docs.spring.io/spring-boot/docs/1.5.9.RELEASE/reference/htmlsingle/#boot-features-embedded-container-servlets-filters-listeners-beans) 

The ServicesInfoProvider implementation should retrieve the POD and Session auth base URL's

#### Spring Boot Starter
To simplify the usage of this library we should provide 'symphony-app-authentication-filter-starter' to enable Spring Boot autoconfiguration. Third-party applications have already been implemented using Spring Boot just needs to declare this starter as dependency.

The library consumer doesn't need to register ServicesInfoProvider implementation in the factory and also doesn't require to register the web filter, the autoconfiguration provided by the library does it for you. We must provide a default implementation for ServicesInfoProvider interface that enables library consumer to describe POD and Session Auth base URL's in the YAML configuration file.

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

###### Motivation
Enable third-party applications to expose API's to allow the app authentication process into the Symphony platform described here.

###### Workflow
There are three basic workflows: authenticate application, validate token pair and validate JWT.

###### Authenticate application
We should provide authentication servlet (AppAuthenticationServlet) to receive HTTP POST requests in order to get the required information (appId and podId) and perform the app authentication in the cloud.

This servlet requires the class implementation to get POD and Session auth base URL's, to retrieve app keystore, and to store the app/symphony tokens. All of them must be provided by the library consumer.

###### Validate token pair
We should provide token validation servlet (TokensValidationServlet) to receive HTTP POST requests in order to get the app and symphony tokens and validate them using the information saved in the previous workflow.

This servlet requires the class implementation to retrieve the app/symphony tokens. It must be provided by the library consumer.

###### Validate JWT
We should provide JWT validation servlet (JwtValidationServlet) to receive HTTP POST requests in order to validate the Json Web Token provided in the request.

JWT validation is quite similar has been described in this document. Actually, we'll reuse the commons components in both implementations.

###### How to use
This section describes the steps how to the third-party application should use this library.

1. Declare symphony-app-authentication-api as dependency in POM file
2. Declare SLF4J implementation as dependency (for example: Log4j, Logback, etc)
3. Implements ServicesInfoProvider interface and register this as default provider in the ServicesInfoProviderFactory
4. Implements KeystoreProvider interface and register this as default provider in the KeystoreProviderFactory
5. Implements StoreTokensProvider interface and register this as default provider in the StoreTokensProviderFactory
6. Register AppAuthenticationServlet, TokensValidationServlet, and JwtValidationServlet as servlets using web.xml descriptor for traditional Java webapps or this approach for Spring boot applications

The ServicesInfoProvider implementation should retrieve the POD and Session auth base URL's

The KeystoreProvider implementation should retrieve the keystore used to perform authentication on the POD reaching out the ```https://YOUR_POD_SUBDOMAIN-api.symphony.com/sessionauth/v1/authenticate``` endpoint.

The StoreTokensProvider implementation should retrieve/store the app and symphony tokens from/to a persistent storage. The library should provide a default implementation using local cache (in-memory), but it's not recommended for production use, only for tests.
