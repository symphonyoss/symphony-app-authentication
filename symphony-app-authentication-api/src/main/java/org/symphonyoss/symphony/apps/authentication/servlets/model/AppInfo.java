package org.symphonyoss.symphony.apps.authentication.servlets.model;

/**
 * Holds the app info to request the authentication.
 *
 * Created by rsanchez on 06/03/18.
 */
public class AppInfo {

  private String appId;

  private String podId;

  public String getAppId() {
    return appId;
  }

  public void setAppId(String appId) {
    this.appId = appId;
  }

  public String getPodId() {
    return podId;
  }

  public void setPodId(String podId) {
    this.podId = podId;
  }
}
