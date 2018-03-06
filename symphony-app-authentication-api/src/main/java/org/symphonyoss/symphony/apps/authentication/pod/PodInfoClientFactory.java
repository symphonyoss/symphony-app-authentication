package org.symphonyoss.symphony.apps.authentication.pod;

import org.symphonyoss.symphony.apps.authentication.AbstractFactory;

/**
 * Factory to build {@link PodInfoClient} component
 *
 * Created by rsanchez on 06/03/18.
 */
public class PodInfoClientFactory extends AbstractFactory<PodInfoClient> {

  private static final PodInfoClientFactory INSTANCE = new PodInfoClientFactory();

  private PodInfoClientFactory() {}

  public static PodInfoClientFactory getInstance() {
    return INSTANCE;
  }

}
