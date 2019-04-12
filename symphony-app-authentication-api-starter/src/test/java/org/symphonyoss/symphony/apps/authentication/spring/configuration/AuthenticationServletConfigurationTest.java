package org.symphonyoss.symphony.apps.authentication.spring.configuration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.symphonyoss.symphony.apps.authentication.certificate.PodCertificateClient;
import org.symphonyoss.symphony.apps.authentication.certificate.PodCertificateClientFactory;
import org.symphonyoss.symphony.apps.authentication.endpoints.ServicesInfoProvider;
import org.symphonyoss.symphony.apps.authentication.endpoints.ServicesInfoProviderFactory;
import org.symphonyoss.symphony.apps.authentication.json.JsonParser;
import org.symphonyoss.symphony.apps.authentication.json.JsonParserFactory;
import org.symphonyoss.symphony.apps.authentication.keystore.KeystoreProvider;
import org.symphonyoss.symphony.apps.authentication.keystore.KeystoreProviderFactory;
import org.symphonyoss.symphony.apps.authentication.spring.properties.AppAuthenticationProperties;
import org.symphonyoss.symphony.apps.authentication.spring.properties
    .AuthenticationServletProperties;
import org.symphonyoss.symphony.apps.authentication.tokens.StoreTokensProvider;
import org.symphonyoss.symphony.apps.authentication.tokens.StoreTokensProviderFactory;

import java.util.Collection;

/**
 * Unit tests for {@link AuthenticationServletConfiguration}
 *
 * Created by rsanchez on 09/03/18.
 */
@RunWith(MockitoJUnitRunner.class)
public class AuthenticationServletConfigurationTest {

  private static final String BASE_PATH = "/v1/application";

  private static final String EXPECTED_AUTHENTICATE_PATH = BASE_PATH + "/authenticate";

  private static final String EXPECTED_TOKENS_PATH = BASE_PATH + "/tokens/validate";

  private static final String EXPECTED_JWT_PATH = BASE_PATH + "/jwt/validate";

  private static final String APP_NAME = "APP_NAME";

  private static final Long EXPIRATION = 30000l;

  private static final Boolean RSA_ENABLED = true;

  private static final Boolean RSA_DISABLED = false;

  private static final String PRIVATE_KEY_PATH = "/testFolder/";

  private static final String PRIVATE_KEY_NAME = "key.pkcs8";

  @Mock
  private JsonParser jsonParser;

  @Mock
  private KeystoreProvider keystoreProvider;

  @Mock
  private ServicesInfoProvider provider;

  @Mock
  private StoreTokensProvider storeTokensProvider;

  @Mock
  private PodCertificateClient client;

  private JsonParserFactory parserFactory = JsonParserFactory.getInstance();

  private KeystoreProviderFactory keystoreProviderFactory = KeystoreProviderFactory.getInstance();

  private ServicesInfoProviderFactory providerFactory = ServicesInfoProviderFactory.getInstance();

  private PodCertificateClientFactory certificateClientFactory = PodCertificateClientFactory.getInstance();

  private StoreTokensProviderFactory storeTokensProviderFactory = StoreTokensProviderFactory.getInstance();

  private AuthenticationServletConfiguration configuration = new AuthenticationServletConfiguration();

  @Test
  public void testAuthenticateServlet_rsaDisabled() {
    AuthenticationServletProperties properties = new AuthenticationServletProperties();
    properties.setBasePath(BASE_PATH);

    AppAuthenticationProperties appAuthenticationProperties = new AppAuthenticationProperties();
    appAuthenticationProperties.setRsaEnabled(RSA_DISABLED);

    ServletRegistrationBean registration = configuration.authenticateServlet(jsonParser,
        keystoreProvider, provider, storeTokensProvider, properties, appAuthenticationProperties);

    Collection<String> urlMappings = registration.getUrlMappings();

    assertFalse(urlMappings.isEmpty());
    assertEquals(EXPECTED_AUTHENTICATE_PATH, urlMappings.iterator().next());

    assertEquals(jsonParser, parserFactory.getComponent());
    assertEquals(keystoreProvider, keystoreProviderFactory.getComponent());
    assertEquals(provider, providerFactory.getComponent());
    assertEquals(storeTokensProvider, storeTokensProviderFactory.getComponent());
  }

  @Test
  public void testAuthenticateServlet_rsaEnabled() {
    AuthenticationServletProperties properties = new AuthenticationServletProperties();
    properties.setBasePath(BASE_PATH);

    AppAuthenticationProperties appAuthenticationProperties = new AppAuthenticationProperties();
    appAuthenticationProperties.setRsaEnabled(RSA_ENABLED);
    appAuthenticationProperties.setAppName(APP_NAME);
    appAuthenticationProperties.setExpiration(EXPIRATION);
    appAuthenticationProperties.setAppPrivateKeyPath(PRIVATE_KEY_PATH);
    appAuthenticationProperties.setAppPrivateKeyName(PRIVATE_KEY_NAME);

    ServletRegistrationBean registration = configuration.authenticateServlet(jsonParser,
        keystoreProvider, provider, storeTokensProvider, properties, appAuthenticationProperties);

    Collection<String> urlMappings = registration.getUrlMappings();

    assertFalse(urlMappings.isEmpty());
    assertEquals(EXPECTED_AUTHENTICATE_PATH, urlMappings.iterator().next());

    assertEquals(jsonParser, parserFactory.getComponent());
    assertEquals(keystoreProvider, keystoreProviderFactory.getComponent());
    assertEquals(provider, providerFactory.getComponent());
    assertEquals(storeTokensProvider, storeTokensProviderFactory.getComponent());
  }

  @Test
  public void testTokensValidationServlet() {
    AuthenticationServletProperties properties = new AuthenticationServletProperties();
    properties.setBasePath(BASE_PATH);

    ServletRegistrationBean registration = configuration.tokenValidationServlet(jsonParser,
        storeTokensProvider, properties);

    Collection<String> urlMappings = registration.getUrlMappings();

    assertFalse(urlMappings.isEmpty());
    assertEquals(EXPECTED_TOKENS_PATH, urlMappings.iterator().next());

    assertEquals(jsonParser, parserFactory.getComponent());
    assertEquals(storeTokensProvider, storeTokensProviderFactory.getComponent());
  }

  @Test
  public void testJwtServlet() {
    AuthenticationServletProperties properties = new AuthenticationServletProperties();
    properties.setBasePath(BASE_PATH);

    ServletRegistrationBean registration = configuration.jwtValidationServlet(jsonParser,
        client, provider, properties);

    Collection<String> urlMappings = registration.getUrlMappings();

    assertFalse(urlMappings.isEmpty());
    assertEquals(EXPECTED_JWT_PATH, urlMappings.iterator().next());

    assertEquals(jsonParser, parserFactory.getComponent());
    assertEquals(client, certificateClientFactory.getComponent());
    assertEquals(provider, providerFactory.getComponent());
  }

}
