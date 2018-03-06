package org.symphonyoss.symphony.apps.authentication.servlets;

import org.symphonyoss.symphony.apps.authentication.tokens.StoreTokensProvider;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet class to receive HTTP POST requests in order to get the app and symphony tokens and
 * validate them according to the information saved in the datastore.
 * <p>
 * This servlet requires the class implementation of {@link StoreTokensProvider} to retrieve the app/symphony
 * tokens.
 * Created by rsanchez on 06/03/18.
 */
public class TokensValidationServlet extends HttpServlet {

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
      IOException {
    // TODO
  }

}
