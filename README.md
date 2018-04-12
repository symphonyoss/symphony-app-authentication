# Symphony Third-Party Authentication

This library allows app authentication into the Symphony platform and also the ability to authenticate Symphony users
 in the third-party application given a JSON Web Token retrieved from the POD.

This document has been splitted in three parts, **Application Authentication Filter**, **Application Authentication
API**, and **Application Authentication Front-End**.

## Application Authentication Filter 

#### Purpose
Enables third-party applications to perform authentication process using Symphony cloud-infrastructure.
This approach allows the third-party application to check the user identity within the Symphony platform.

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
3. Implement the ServicesInfoProvider interface and register this as the default provider in the ServicesInfoProviderFactory **(optional)**
4. Register 'org.symphonyoss.integration.application.authentication.jwt.JwtFilter' as a web filter using web.xml descriptor for traditional Java webapps. For Spring boot applications, follow [this guide](https://docs.spring.io/spring-boot/docs/1.5.9.RELEASE/reference/htmlsingle/#boot-features-embedded-container-servlets-filters-listeners-beans). 

The ServicesInfoProvider implementation should retrieve the POD and Session auth base URL's. There is a default
implementation to get those values from environment variables (EnvPropertiesServicesInfoProvider)

#### Spring Boot Starter
To simplify the usage of this library we provide the 'symphony-app-authentication-filter-starter' to enable Spring Boot autoconfiguration. Third-party applications that have already been implemented using Spring Boot just need to declare this starter as a dependency.

The library consumer doesn't need to register the ServicesInfoProvider implementation in the factory and also doesn't need to register the web filter - the autoconfiguration provided by the library does it for you.
The default implementation for the ServicesInfoProvider interface (SpringServiceInfoProvider) enables the library
consumer to describe the POD and Session Auth base URL's in the YAML configuration file.

###### Example:

```yaml
app-authentication:
  enabled: true
  pod:
    host: test.symphony.com
    port: 443
  session_auth:
    host: test-api.symphony.com
    port: 443
  filter:
    url-patterns:
      - "/v1/*"
    excluded-paths:
      - "/v1/application/authenticate"
      - "/v1/application/tokens/validate"
      - "/v1/application/jwt/validate"
```

## Application Authentication API

#### Purpose
Enables third-party applications to expose the API's to allow the app authentication process into the Symphony platform.

#### Workflow
There are three steps in the workflow:

1. Authenticate application
2. Validate token pair
3. Validate JWT

#### Authenticate application
Provides authentication servlet (AppAuthenticationServlet) to receive HTTP POST requests in order to get the required information (appId) and perform the app authentication in the cloud.

This servlet requires the class implementation to get POD and Session auth base URL's, to retrieve app keystore, and to store the app/symphony tokens. All of them must be provided by the library consumer.

#### Validate token pair
Provides token validation servlet (TokensValidationServlet) to receive HTTP POST requests in order to get the app and symphony tokens and validate them using the information saved in the previous step.

This servlet requires the class implementation to retrieve the app/symphony tokens. It must be provided by the library consumer.

#### Validate JWT
Provides JWT validation servlet (JwtValidationServlet) to receive HTTP POST requests in order to validate the Json Web Token provided in the request.

JWT validation is quite similar has been described in this file above. Actually, we'll reuse the commons components in both implementations.

#### How to use
This section describes the steps how to the third-party application should use this library.

1. Declare symphony-app-authentication-api as dependency in POM file
2. Declare SLF4J implementation as dependency (for example: Log4j, Logback, etc)
3. Implements ServicesInfoProvider interface and register this as default provider in the ServicesInfoProviderFactory **(optional)**
4. Implements KeystoreProvider interface and register this as default provider in the KeystoreProviderFactory
5. Implements StoreTokensProvider interface and register this as default provider in the StoreTokensProviderFactory
6. Register AppAuthenticationServlet, TokensValidationServlet, and JwtValidationServlet as servlets using web.xml descriptor for traditional Java webapps. For Spring boot applications, follow [this guide](https://docs.spring.io/spring-boot/docs/1.5.9.RELEASE/reference/htmlsingle/#boot-features-embedded-container-servlets-filters-listeners-beans).

The ServicesInfoProvider implementation should retrieve the POD and Session auth base URL's. There is a default
implementation to get those values from environment variables (EnvPropertiesServicesInfoProvider)

The KeystoreProvider implementation should retrieve the keystore used to perform authentication on the POD reaching out the ```https://YOUR_POD_SUBDOMAIN-api.symphony.com/sessionauth/v1/authenticate``` endpoint.

The StoreTokensProvider implementation should retrieve/store the app and symphony tokens from/to a persistent storage.

The library provides a default implementation using local cache (in-memory), but it's not recommended for production
use, only for tests.

#### Spring Boot Starter
To simplify the usage of this library there is a 'symphony-app-authentication-api-starter' to enable Spring Boot autoconfiguration. Third-party applications have already been implemented using Spring Boot just needs to declare this starter as dependency and creates spring components implementing the required interfaces described above.

The library consumer doesn't need to register those implementations in the factories and also doesn't require to register the servlets, the autoconfiguration provided by the library does it for you.

The default implementation for the ServicesInfoProvider interface (SpringServiceInfoProvider) enables the library
consumer to describe the POD and Session Auth base URL's in the YAML configuration file.

###### Example:

```yaml
app-authentication:
  enabled: true
  pod:
    host: test.symphony.com
    port: 443
  session_auth:
    host: test-api.symphony.com
    port: 443
  api:
    enabled: true
    base-path: "/v1/application"
```

## Application Authentication Front-End

#### Purpose
This library is responsible for triggering the app authentication process into the Symphony platform by performing API
calls to **Application Authentication API** and registering the application controller.

#### Workflow
There are seven steps in the whole workflow:

1. Symphony Hello
2. Authenticate application
3. Register application controller
4. Validate token pair
5. Retrieve JWT
6. Validate JWT
7. Cache JWT

#### Symphony Hello

The `SYMPHONY.remote.hello()` method should be used to initialize the connection to the Client Extension API from your application controller.

#### Authenticate application

This process reaches out the **Application Authentication API** in order to authenticate the application by providing
only the appId.

#### Register application controller

The `SYMPHONY.application.register()` registers your application controller into the Symphony client. Additionally,
subscribe the application to remote services and register local services that can be used remotely.

To achieve this, the library must provide the application token generated in the previous step and also a list of
required external dependencies as well as a list of dependencies to be exported.

#### Validate token pair

This process reaches out the **Application Authentication API** in order to validate tokens generated in the steps 2
and 3.

#### Retrieve JWT

User identity information can be obtained through the `getJwt()` method on the `extended-user-info` service
in the form of JSON Web Token (JWT).

#### Validate JWT

Your app backend can validate the authenticity of the JWT returned by Symphony's frontend using the public certificate that was used to sign the JWT.

To achieve this, the library must reach out the **Application Authentication API** to validate JWT.

#### Cache JWT

Once the JWT returned by Symphony's frontend has been validated in the previous step, the library must cache it
locally to allow your application to retrieve this token when it requires.

#### How to use

To use the Client Extension API services, you must include the symphony-api.js JavaScript file in your application controller.

```html
<script type="text/javascript" src="https://www.symphony.com/resources/api/v1.0/symphony-api.js" charset="utf-8"></script>
```

You must also import `initApp` function from `symphony-app-authentication-fe` library in your javascript code.

Then, in your application bootstrap, you must call `initApp` function providing a JSON object that contains appId,
external dependencies, local dependencies you want to export, and base URL for **Application Authentication API**

```javascript
import { initApp } from 'symphony-app-authentication-fe';

const appId = 'testapp';
const controllerName = `${appId}:controller`;
const authenticationURL = 'https://localhost:8081/helpdesk';

const config = {
  appId,
  dependencies: ['ui', 'entity'],
  exportedDependencies: [controllerName],
  baseAuthenticationUrl: authenticationURL,
};

const bootstrap = () => {
  // Do your stuff
};

SYMPHONY.services.register(controllerName);

initApp(config)
  .then(() => bootstrap())
  .fail(e => console.error(e));
```

Once you have initialized your application, you're able to get user JWT. You should call `getUserJWT` function
from `symphony-app-authentication-fe` library.

```javascript
import { getUserJWT } from 'symphony-app-authentication-fe';

const jwt = getUserJWT();
```

More information about the app authentication see [here](https://extension-api.symphony.com/docs/application-authentication)


