package org.symphonyoss.symphony.apps.authentication.spring.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Base properties for the authentication stuff.
 *
 * Created by rsanchez on 10/01/18.
 */
@Configuration
@ConfigurationProperties(prefix = "app-authentication")
public class AuthenticationProperties {

  private HttpClientProperties httpClient;

  private CacheProperties cache;

  private ServiceAddress pod;

  private ServiceAddress sessionAuth;

  private long expiration;

  private boolean rsaEnabled;

  private String appPrivateKeyPath;

  private String appPrivateKeyName;

  private String appName;

  public HttpClientProperties getHttpClient() {
    return httpClient;
  }

  public void setHttpClient(HttpClientProperties httpClient) {
    this.httpClient = httpClient;
  }

  public CacheProperties getCache() {
    return cache;
  }

  public void setCache(CacheProperties cache) {
    this.cache = cache;
  }

  public ServiceAddress getPod() {
    return pod;
  }

  public void setPod(ServiceAddress pod) {
    this.pod = pod;
  }

  public ServiceAddress getSessionAuth() {
    return sessionAuth;
  }

  public void setSessionAuth(
      ServiceAddress sessionAuth) {
    this.sessionAuth = sessionAuth;
  }

  public Long getExpiration() {
    return expiration;
  }

  public void setExpiration(long expiration) {
    this.expiration = expiration;
  }

  public Boolean isRsaEnabled() {
    return rsaEnabled;
  }

  public void setRsaEnabled(boolean rsaEnabled) {
    this.rsaEnabled = rsaEnabled;
  }

  public String getAppPrivateKeyPath() {
    return appPrivateKeyPath;
  }

  public void setAppPrivateKeyPath(String appPrivateKeyPath) {
    this.appPrivateKeyPath = appPrivateKeyPath;
  }

  public String getAppPrivateKeyName() {
    return appPrivateKeyName;
  }

  public void setAppPrivateKeyName(String appPrivateKeyName) {
    this.appPrivateKeyName = appPrivateKeyName;
  }

  public String getAppName() {
    return appName;
  }

  public void setAppName(String appName) {
    this.appName = appName;
  }

}
