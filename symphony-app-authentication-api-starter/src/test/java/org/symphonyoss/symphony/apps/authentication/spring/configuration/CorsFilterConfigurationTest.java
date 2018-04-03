package org.symphonyoss.symphony.apps.authentication.spring.configuration;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.symphonyoss.symphony.apps.authentication.filter.CorsFilter;
import org.symphonyoss.symphony.apps.authentication.spring.properties
    .AuthenticationServletProperties;

import java.util.Collection;

/**
 * Unit tests for {@link CorsFilterConfiguration}
 * Created by rsanchez on 03/04/18.
 */
public class CorsFilterConfigurationTest {

  private static final String MOCK_BASE_PATH = "/v1/application";

  @Test
  public void testConfiguration() {
    AuthenticationServletProperties properties = new AuthenticationServletProperties();
    properties.setBasePath(MOCK_BASE_PATH);

    CorsFilterConfiguration configuration = new CorsFilterConfiguration();

    FilterRegistrationBean registrationBean = configuration.corsFilter(properties);

    assertEquals(CorsFilter.class, registrationBean.getFilter().getClass());

    Collection<String> urlPatterns = registrationBean.getUrlPatterns();

    assertEquals(1, urlPatterns.size());
    assertEquals("/v1/application/*", urlPatterns.iterator().next());
  }

}
