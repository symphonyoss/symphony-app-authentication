package org.symphonyoss.symphony.apps.authentication.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet class to receive HTTP POST requests in order to validate the JSON Web Token provided
 * in the request.
 * <p>
 * Created by rsanchez on 06/03/18.
 */
public class JwtValidationServlet extends HttpServlet {

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
      IOException {
    // TODO
  }

}
