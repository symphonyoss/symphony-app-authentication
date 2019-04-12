package org.symphonyoss.symphony.apps.authentication;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.codec.binary.Hex;
import org.symphonyoss.symphony.apps.authentication.tokens.StoreTokensProvider;
import org.symphonyoss.symphony.apps.authentication.tokens.StoreTokensProviderFactory;
import org.symphonyoss.symphony.apps.authentication.tokens.model.AppToken;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

/**
 * Service class to perform app authentication.
 *
 * Created by rsanchez on 06/03/18.
 */
public class AppAuthenticationService {

  private SecureRandom secureRandom = new SecureRandom();

  private AppAuthenticationApiClient apiClient = new AppAuthenticationApiClient();

  private StoreTokensProviderFactory factory = StoreTokensProviderFactory.getInstance();

  private static final String PEM_PRIVATE_START = "-----BEGIN PRIVATE KEY-----";
  private static final String PEM_PRIVATE_END = "-----END PRIVATE KEY-----";

  /**
   * Start the JWT authentication between the App and the POD.
   * @param appId Application identifier.
   * @return The generated Application Token (Ta).
   * @throws AppAuthenticationException Failure to authenticate application
   */
  public AppToken authenticate(String appId) throws AppAuthenticationException {
    String appToken = generateToken();

    AppToken result = apiClient.authenticate(appId, appToken, null);

    StoreTokensProvider provider = factory.getComponent();
    provider.saveAppAuthenticationToken(result);

    return result;
  }

  /**
   * Start the JWT authentication between the App and the POD.
   * @param appId Application identifier.
   * @return The generated Application Token (Ta).
   * @throws AppAuthenticationException Failure to authenticate application
   */
  public AppToken rsaAuthenticate(String appId, String appName, String privateKeyFile,
      long expiration) throws AppAuthenticationException {
    String jwt;
    try {
      jwt = createSignedJwt(appName, expiration, parseRSAPrivateKey(new File(privateKeyFile)));
    } catch (GeneralSecurityException e) {
      throw new AppAuthenticationException(e.getMessage(), e);
    }
    String appToken = generateToken();

    AppToken result = apiClient.authenticate(appId, appToken, jwt);

    StoreTokensProvider provider = factory.getComponent();
    provider.saveAppAuthenticationToken(result);

    return result;
  }

  /**
   * Generate a 64-bit security-safe String random token.
   * @return String random token.
   */
  private String generateToken() {
    byte[] randBytes = new byte[64];
    secureRandom.nextBytes(randBytes);
    return Hex.encodeHexString(randBytes);
  }

  /**
   * Validate if the Symphony previously generated token by the app token and the POD token are
   * valid.
   * @param applicationToken App token generated by the application.
   * @param symphonyToken Symphony token generated by the POD.
   * @return <code>true</code> if the token pair is valid.
   */
  public boolean isValidTokenPair(String applicationToken, String symphonyToken) {
    StoreTokensProvider provider = factory.getComponent();

    AppToken token = provider.getAppAuthenticationToken(applicationToken);

    if (token == null) {
      return false;
    }

    return symphonyToken.equals(token.getSymphonyToken());
  }

  /**
   * Creates a JWT with the provided user name and expiration date, signed with the provided private
   * key.
   * @param appName the APP NAME to authenticate; will be verified by the pod
   * @param expiration of the authentication request in milliseconds; cannot be longer than the
   * value defined on the pod
   * @param privateKey the private RSA key to be used to sign the authentication request; will be
   * checked on the pod against
   * the public key stored for the user
   */
  private String createSignedJwt(String appName, long expiration, Key privateKey) {
    return Jwts.builder()
        .setSubject(appName)
        .setExpiration(new Date(System.currentTimeMillis() + expiration))
        .signWith(SignatureAlgorithm.RS512, privateKey)
        .compact();
  }

  /**
   * Create a RSA Private Key from a PEM String. It supports PKCS#8 string format
   * @param pemPrivateKeyFile
   * @return {@link PrivateKey}
   * @throws GeneralSecurityException
   */
  private PrivateKey parseRSAPrivateKey(final File pemPrivateKeyFile)
      throws GeneralSecurityException {
    try {
      String pemPrivateKey =
          new String(Files.readAllBytes(Paths.get(pemPrivateKeyFile.getAbsolutePath())));
      if (pemPrivateKey.contains(PEM_PRIVATE_START)) {
        String privateKeyString = pemPrivateKey
            .replace(PEM_PRIVATE_START, "")
            .replace(PEM_PRIVATE_END, "")
            .replace("\\n", "\n")
            .replaceAll("\\s", "");
        byte[] keyBytes = Base64.getDecoder().decode(privateKeyString.getBytes("UTF-8"));
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory fact = KeyFactory.getInstance("RSA");
        return fact.generatePrivate(keySpec);
      }
      throw new GeneralSecurityException("Invalid private key.");

    } catch (InvalidKeySpecException | NoSuchAlgorithmException | IOException e) {
      throw new GeneralSecurityException(e);
    }
  }

}
