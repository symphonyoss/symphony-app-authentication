package org.symphonyoss.symphony.apps.authentication.servlets;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.symphonyoss.symphony.apps.authentication.servlets.AppBaseServlet.APPLICATION_JSON;
import static org.symphonyoss.symphony.apps.authentication.servlets.AppBaseServlet.CONTENT_TYPE;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.symphony.apps.authentication.AppAuthenticationException;
import org.symphonyoss.symphony.apps.authentication.AppAuthenticationService;
import org.symphonyoss.symphony.apps.authentication.json.JacksonParser;
import org.symphonyoss.symphony.apps.authentication.json.JsonParser;
import org.symphonyoss.symphony.apps.authentication.json.JsonParserFactory;
import org.symphonyoss.symphony.apps.authentication.servlets.model.ErrorResponse;
import org.symphonyoss.symphony.apps.authentication.tokens.model.AppToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Unit test for {@link AppAuthenticationServlet}
 *
 * Created by rsanchez on 07/03/18.
 */
@RunWith(MockitoJUnitRunner.class)
public class AppAuthenticationServletTest {

  private static final String APP_ID = "APP_ID";

  private static final String APP_TOKEN = "ABCD";

  private static final String MOCK_APP_INFO = "{ \"appId\": \"APP_ID\" }";

  private static final String RSA_ENABLED = "rsa-enabled";

  private static final String FALSE_STRING = "false";

  private static final String TRUE_STRING = "true";

  private static final String EXPIRATION_VALUE_AS_STRING = "30000";

  private static final Long EXPIRATION_VALUE_AS_LONG = 30000l;

  public static final String EXPIRATION_PARAM_TITLE = "expiration";

  private static final String APP_NAME = "APP_NAME";

  private static final String APP_NAME_PARAM_TITLE = "appName";

  private static final String PRIVATE_KEY_FILENAME = "/testFolder/key.pkcs8";

  private static final String PRIVATE_KEY_PARAM_TITLE = "privateKey";

  @Mock
  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  @Mock
  private BufferedReader reader;

  @Mock
  private PrintWriter writer;

  @Mock
  private AppAuthenticationService authenticationService;

  @Mock
  private ServletConfig servletConfig;

  @InjectMocks
  private AppAuthenticationServlet servlet;

  @BeforeClass
  public static void setup() {
    JsonParserFactory.getInstance().setComponent(new JacksonParser());
  }

  @Before
  public void init() throws IOException, ServletException {
    doReturn(reader).when(request).getReader();
    doReturn(writer).when(response).getWriter();
  }

  @Test
  public void testMissingAppId() throws IOException, ServletException {
    doReturn("{}").doReturn(null).when(reader).readLine();

    servlet.doPost(request, response);

    ErrorResponse errorResponse = new ErrorResponse();
    errorResponse.setCode(HttpServletResponse.SC_BAD_REQUEST);
    errorResponse.setMessage("Missing the required parameter appId");

    JsonParser parser = JsonParserFactory.getInstance().getComponent();
    String expected = parser.writeToString(errorResponse);

    verify(response, times(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    verify(writer, times(1)).write(expected);
    verify(response, times(1)).addHeader(CONTENT_TYPE, APPLICATION_JSON);
  }

  @Test
  public void testFailureAuthentication()
      throws IOException, ServletException, AppAuthenticationException {
    String errorMessage = "Failure to authenticate";
    AppAuthenticationException exception = new AppAuthenticationException(errorMessage);

    doReturn(MOCK_APP_INFO).doReturn(null).when(reader).readLine();
    doThrow(exception).when(authenticationService).authenticate(APP_ID);
    doReturn(FALSE_STRING).when(servlet.getServletConfig()).getInitParameter(RSA_ENABLED);

    servlet.doPost(request, response);

    ErrorResponse errorResponse = new ErrorResponse();
    errorResponse.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    errorResponse.setMessage(errorMessage);

    JsonParser parser = JsonParserFactory.getInstance().getComponent();
    String expected = parser.writeToString(errorResponse);

    verify(response, times(1)).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    verify(writer, times(1)).write(expected);
    verify(response, times(1)).addHeader(CONTENT_TYPE, APPLICATION_JSON);
  }

  @Test
  public void testFailureAuthentication_rsaEnabled()
      throws IOException, ServletException, AppAuthenticationException {
    String errorMessage = "Failure to authenticate";
    AppAuthenticationException exception = new AppAuthenticationException(errorMessage);

    doReturn(MOCK_APP_INFO).doReturn(null).when(reader).readLine();
    doThrow(exception).when(authenticationService).rsaAuthenticate(APP_ID, APP_NAME, PRIVATE_KEY_FILENAME, EXPIRATION_VALUE_AS_LONG);
    doReturn(TRUE_STRING).when(servlet.getServletConfig()).getInitParameter(RSA_ENABLED);
    doReturn(EXPIRATION_VALUE_AS_STRING).when(servlet.getServletConfig())
        .getInitParameter(EXPIRATION_PARAM_TITLE);
    doReturn(APP_NAME).when(servlet.getServletConfig())
        .getInitParameter(APP_NAME_PARAM_TITLE);
    doReturn(PRIVATE_KEY_FILENAME).when(servlet.getServletConfig())
        .getInitParameter(PRIVATE_KEY_PARAM_TITLE);

    servlet.doPost(request, response);

    ErrorResponse errorResponse = new ErrorResponse();
    errorResponse.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    errorResponse.setMessage(errorMessage);

    JsonParser parser = JsonParserFactory.getInstance().getComponent();
    String expected = parser.writeToString(errorResponse);

    verify(response, times(1)).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    verify(writer, times(1)).write(expected);
    verify(response, times(1)).addHeader(CONTENT_TYPE, APPLICATION_JSON);
  }

  @Test
  public void testAuthenticate() throws IOException, ServletException, AppAuthenticationException {
    AppToken appToken = new AppToken();
    appToken.setAppId(APP_ID);
    appToken.setAppToken(APP_TOKEN);

    doReturn(MOCK_APP_INFO).doReturn(null).when(reader).readLine();
    doReturn(appToken).when(authenticationService).authenticate(APP_ID);
    doReturn(FALSE_STRING).when(servlet.getServletConfig()).getInitParameter(RSA_ENABLED);

    servlet.doPost(request, response);

    JsonParser parser = JsonParserFactory.getInstance().getComponent();
    String expected = parser.writeToString(appToken);

    verify(response, times(1)).setStatus(HttpServletResponse.SC_OK);
    verify(writer, times(1)).write(expected);
    verify(response, times(1)).addHeader(CONTENT_TYPE, APPLICATION_JSON);
  }

  @Test
  public void testAuthenticate_rsaEnabled()
      throws IOException, ServletException, AppAuthenticationException {
    AppToken appToken = new AppToken();
    appToken.setAppId(APP_ID);
    appToken.setAppToken(APP_TOKEN);

    doReturn(MOCK_APP_INFO).doReturn(null).when(reader).readLine();
    doReturn(appToken).when(authenticationService).rsaAuthenticate(APP_ID, APP_NAME, PRIVATE_KEY_FILENAME, EXPIRATION_VALUE_AS_LONG);
    doReturn(TRUE_STRING).when(servlet.getServletConfig()).getInitParameter(RSA_ENABLED);
    doReturn(EXPIRATION_VALUE_AS_STRING).when(servlet.getServletConfig())
        .getInitParameter(EXPIRATION_PARAM_TITLE);
    doReturn(APP_NAME).when(servlet.getServletConfig())
        .getInitParameter(APP_NAME_PARAM_TITLE);
    doReturn(PRIVATE_KEY_FILENAME).when(servlet.getServletConfig())
        .getInitParameter(PRIVATE_KEY_PARAM_TITLE);

    servlet.doPost(request, response);

    JsonParser parser = JsonParserFactory.getInstance().getComponent();
    String expected = parser.writeToString(appToken);

    verify(response, times(1)).setStatus(HttpServletResponse.SC_OK);
    verify(writer, times(1)).write(expected);
    verify(response, times(1)).addHeader(CONTENT_TYPE, APPLICATION_JSON);
  }
}
