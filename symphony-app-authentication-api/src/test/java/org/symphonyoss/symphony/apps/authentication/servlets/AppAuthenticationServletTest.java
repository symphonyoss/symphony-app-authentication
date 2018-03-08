package org.symphonyoss.symphony.apps.authentication.servlets;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
import org.symphonyoss.symphony.apps.authentication.pod.PodInfoClient;
import org.symphonyoss.symphony.apps.authentication.pod.PodInfoClientFactory;
import org.symphonyoss.symphony.apps.authentication.pod.model.PodInfo;
import org.symphonyoss.symphony.apps.authentication.servlets.model.ErrorResponse;
import org.symphonyoss.symphony.apps.authentication.tokens.model.AppToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

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

  private static final String POD_ID = "123";

  private static final String MOCK_APP_INFO = "{ \"appId\": \"APP_ID\", \"podId\": \"123\" }";

  private static final String MISSING_POD_ID = "{ \"appId\": \"APP_ID\" }";

  @Mock
  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  @Mock
  private BufferedReader reader;

  @Mock
  private PrintWriter writer;

  @Mock
  private PodInfoClient podInfoClient;

  @Mock
  private AppAuthenticationService authenticationService;

  @InjectMocks
  private AppAuthenticationServlet servlet;

  @BeforeClass
  public static void setup() {
    JsonParserFactory.getInstance().setComponent(new JacksonParser());
  }

  @Before
  public void init() throws IOException {
    PodInfoClientFactory.getInstance().setComponent(podInfoClient);

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
  }

  @Test
  public void testMissingPodId() throws IOException, ServletException {
    doReturn(MISSING_POD_ID).doReturn(null).when(reader).readLine();

    servlet.doPost(request, response);

    ErrorResponse errorResponse = new ErrorResponse();
    errorResponse.setCode(HttpServletResponse.SC_BAD_REQUEST);
    errorResponse.setMessage("Missing the required parameter podId");

    JsonParser parser = JsonParserFactory.getInstance().getComponent();
    String expected = parser.writeToString(errorResponse);

    verify(response, times(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    verify(writer, times(1)).write(expected);
  }

  @Test
  public void testInvalidPodId()
      throws IOException, ServletException, AppAuthenticationException {
    doReturn(MOCK_APP_INFO).doReturn(null).when(reader).readLine();
    doReturn(new PodInfo()).when(podInfoClient).getPodInfo(APP_ID);

    servlet.doPost(request, response);

    ErrorResponse errorResponse = new ErrorResponse();
    errorResponse.setCode(HttpServletResponse.SC_UNAUTHORIZED);
    errorResponse.setMessage("The provided POD ID 123 is not the same used by this application.");

    JsonParser parser = JsonParserFactory.getInstance().getComponent();
    String expected = parser.writeToString(errorResponse);

    verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    verify(writer, times(1)).write(expected);
  }

  @Test
  public void testFailureAuthentication()
      throws IOException, ServletException, AppAuthenticationException {
    String errorMessage = "Failure to authenticate";
    AppAuthenticationException exception = new AppAuthenticationException(errorMessage);

    PodInfo info = new PodInfo();
    info.setPodId(POD_ID);

    doReturn(MOCK_APP_INFO).doReturn(null).when(reader).readLine();
    doReturn(info).when(podInfoClient).getPodInfo(APP_ID);
    doThrow(exception).when(authenticationService).authenticate(APP_ID);

    servlet.doPost(request, response);

    ErrorResponse errorResponse = new ErrorResponse();
    errorResponse.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    errorResponse.setMessage(errorMessage);

    JsonParser parser = JsonParserFactory.getInstance().getComponent();
    String expected = parser.writeToString(errorResponse);

    verify(response, times(1)).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    verify(writer, times(1)).write(expected);
  }

  @Test
  public void testAuthenticate() throws IOException, ServletException, AppAuthenticationException {
    PodInfo info = new PodInfo();
    info.setPodId(POD_ID);

    AppToken appToken = new AppToken();
    appToken.setAppId(APP_ID);
    appToken.setAppToken(APP_TOKEN);

    doReturn(MOCK_APP_INFO).doReturn(null).when(reader).readLine();
    doReturn(info).when(podInfoClient).getPodInfo(APP_ID);
    doReturn(appToken).when(authenticationService).authenticate(APP_ID);

    servlet.doPost(request, response);

    JsonParser parser = JsonParserFactory.getInstance().getComponent();
    String expected = parser.writeToString(appToken);

    verify(response, times(1)).setStatus(HttpServletResponse.SC_OK);
    verify(writer, times(1)).write(expected);
  }
}
