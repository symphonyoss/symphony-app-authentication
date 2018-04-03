package org.symphonyoss.symphony.apps.authentication.spring.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.symphonyoss.symphony.apps.authentication.filter.CorsFilter;
import org.symphonyoss.symphony.apps.authentication.spring.properties
    .AuthenticationServletProperties;

import java.util.Collections;

/**
 * Spring Configuration to create CORS filter
 *
 * Created by rsanchez on 02/04/18.
 */
@Configuration
@ConditionalOnProperty(name = "app-authentication.enabled", havingValue = "true")
@EnableConfigurationProperties({AuthenticationServletProperties.class})
public class CorsFilterConfiguration {

  @Bean
  @ConditionalOnProperty(name = "app-authentication.api.enabled", havingValue = "true")
  public FilterRegistrationBean corsFilter(AuthenticationServletProperties servletProperties) {
    CorsFilter filter = new CorsFilter();

    FilterRegistrationBean registration = new FilterRegistrationBean(filter);

    String basePath = servletProperties.getBasePath();

    registration.setUrlPatterns(Collections.singletonList(basePath + "/*"));

    return registration;
  }

}
