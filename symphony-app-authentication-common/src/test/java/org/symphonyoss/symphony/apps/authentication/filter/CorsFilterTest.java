package org.symphonyoss.symphony.apps.authentication.filter;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.symphonyoss.symphony.apps.authentication.filter.CorsFilter
    .ACCESS_CONTROL_ALLOW_CREDENTIALS;
import static org.symphonyoss.symphony.apps.authentication.filter.CorsFilter
    .ACCESS_CONTROL_ALLOW_HEADERS;
import static org.symphonyoss.symphony.apps.authentication.filter.CorsFilter
    .ACCESS_CONTROL_ALLOW_METHODS;
import static org.symphonyoss.symphony.apps.authentication.filter.CorsFilter
    .ACCESS_CONTROL_ALLOW_ORIGIN;
import static org.symphonyoss.symphony.apps.authentication.filter.CorsFilter.ALLOW_METHODS;
import static org.symphonyoss.symphony.apps.authentication.filter.CorsFilter.CONTENT_TYPE_HEADER;
import static org.symphonyoss.symphony.apps.authentication.filter.CorsFilter.OPTIONS_METHOD;
import static org.symphonyoss.symphony.apps.authentication.filter.CorsFilter.ORIGIN_HEADER;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Unit tests for {@link CorsFilter}
 * Created by rsanchez on 03/04/18.
 */
@RunWith(MockitoJUnitRunner.class)
public class CorsFilterTest {

  private static final String MOCK_ORIGIN = "https://localhost:8081";

  private static final String GET_METHOD = "GET";

  @Mock
  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  @Mock
  private FilterChain chain;

  private CorsFilter corsFilter = new CorsFilter();

  @Test
  public void testWithoutOrigin() throws IOException, ServletException {
    corsFilter.doFilter(request, response, chain);

    verify(response, times(1)).setStatus(HttpServletResponse.SC_FORBIDDEN);
    verify(chain, never()).doFilter(request, response);
  }

  @Test
  public void testOptionsMethod() throws IOException, ServletException {
    doReturn(MOCK_ORIGIN).when(request).getHeader(ORIGIN_HEADER);
    doReturn(OPTIONS_METHOD).when(request).getMethod();

    corsFilter.doFilter(request, response, chain);

    verify(response, times(1)).setStatus(HttpServletResponse.SC_OK);
    verify(response, times(1)).addHeader(ACCESS_CONTROL_ALLOW_ORIGIN, MOCK_ORIGIN);
    verify(response, times(1)).addHeader(ACCESS_CONTROL_ALLOW_METHODS, ALLOW_METHODS);
    verify(response, times(1)).addHeader(ACCESS_CONTROL_ALLOW_CREDENTIALS, Boolean.TRUE.toString());
    verify(response, times(1)).addHeader(ACCESS_CONTROL_ALLOW_HEADERS, CONTENT_TYPE_HEADER);
    verify(chain, never()).doFilter(request, response);
  }

  @Test
  public void testGetMethod() throws IOException, ServletException {
    doReturn(MOCK_ORIGIN).when(request).getHeader(ORIGIN_HEADER);
    doReturn(GET_METHOD).when(request).getMethod();

    corsFilter.doFilter(request, response, chain);

    verify(response, never()).setStatus(HttpServletResponse.SC_OK);
    verify(response, times(1)).addHeader(ACCESS_CONTROL_ALLOW_ORIGIN, MOCK_ORIGIN);
    verify(response, times(1)).addHeader(ACCESS_CONTROL_ALLOW_METHODS, ALLOW_METHODS);
    verify(response, times(1)).addHeader(ACCESS_CONTROL_ALLOW_CREDENTIALS, Boolean.TRUE.toString());
    verify(response, times(1)).addHeader(ACCESS_CONTROL_ALLOW_HEADERS, CONTENT_TYPE_HEADER);
    verify(chain, times(1)).doFilter(request, response);
  }
}
