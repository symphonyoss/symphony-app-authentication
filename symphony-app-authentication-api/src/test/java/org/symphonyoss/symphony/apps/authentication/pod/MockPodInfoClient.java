package org.symphonyoss.symphony.apps.authentication.pod;

import org.symphonyoss.symphony.apps.authentication.pod.model.PodInfo;

/**
 * Mock class for {@link PodInfoClient}
 *
 * Created by rsanchez on 06/03/18.
 */
public class MockPodInfoClient implements PodInfoClient {

  @Override
  public PodInfo getPodInfo(String appId) {
    return null;
  }
}
