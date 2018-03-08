package org.symphonyoss.symphony.apps.authentication.pod;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * Unit tests for {@link PodInfoClientFactory}
 *
 * Created by robson on 06/03/18.
 */
public class PodInfoClientFactoryTest {

  private PodInfoClient client = new MockPodInfoClient();

  private PodInfoClientFactory factory = PodInfoClientFactory.getInstance();

  @Test
  public void testNullComponent() {
    try {
      factory.setComponent(null);
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals("Invalid component implementation. It mustn't be null", e.getMessage());
    }
  }

  @Test
  public void testFactory() {
    factory.setComponent(client);
    assertEquals(client, factory.getComponent());
  }

}
