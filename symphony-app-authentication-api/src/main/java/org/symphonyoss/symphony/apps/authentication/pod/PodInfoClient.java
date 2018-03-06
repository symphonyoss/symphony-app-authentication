package org.symphonyoss.symphony.apps.authentication.pod;

import org.symphonyoss.symphony.apps.authentication.pod.model.PodInfo;

/**
 * Interface to implement HTTP Client to retrieve POD info.
 *
 * Created by rsanchez on 06/03/18.
 */
public interface PodInfoClient {

  PodInfo getPodInfo(String appId);

}
