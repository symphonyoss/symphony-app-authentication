package org.symphonyoss.symphony.apps.authentication;

import org.apache.commons.codec.binary.Hex;
import org.symphonyoss.symphony.apps.authentication.tokens.StoreTokensProvider;
import org.symphonyoss.symphony.apps.authentication.tokens.StoreTokensProviderFactory;
import org.symphonyoss.symphony.apps.authentication.tokens.model.AppToken;

import java.security.SecureRandom;

/**
 * Service class to perform app authentication.
 *
 * Created by rsanchez on 06/03/18.
 */
public class AppAuthenticationService {

  private final SecureRandom secureRandom = new SecureRandom();

  private final AppAuthenticationApiClient apiClient = new AppAuthenticationApiClient();

  private final StoreTokensProviderFactory factory = StoreTokensProviderFactory.getInstance();

  /**
   * Start the JWT authentication between the App and the POD.
   *
   * @param appId Application identifier.
   * @return The generated Application Token (Ta).
   * @throws AppAuthenticationException Failure to authenticate application
   */
  public AppToken authenticate(String appId) throws AppAuthenticationException {
    String appToken = generateToken();

    AppToken result = apiClient.authenticate(appId, appToken);

    StoreTokensProvider provider = factory.getComponent();
    provider.saveAppAuthenticationToken(result);

    return result;
  }

  /**
   * Generate a 64-bit security-safe String random token.
   * @return  String random token.
   */
  private String generateToken() {
    byte[] randBytes = new byte[64];
    secureRandom.nextBytes(randBytes);
    return Hex.encodeHexString(randBytes);
  }

}
