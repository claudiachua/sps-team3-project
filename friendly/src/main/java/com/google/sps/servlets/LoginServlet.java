// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.sps.model.LoginStatus;
import com.google.sps.util.JsonUtility;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json");
    PrintWriter out = response.getWriter();
    UserService userService = UserServiceFactory.getUserService();

    LoginStatus loginStatus = new LoginStatus();

    String loginUrl = userService.createLoginURL("/index");
    loginStatus.loginUrl = loginUrl;

    // If user is not logged in, show a login form (could also redirect to a login page)
    if (!userService.isUserLoggedIn()) {
        loginStatus.loginStatus = false;
    } else {
        loginStatus.loginStatus = true;
        String userEmail = userService.getCurrentUser().getEmail();
        loginStatus.userEmail = userEmail;

    /**
     *If the user is not in the datastore, put the user into the datastore 
     *thus the user could be inside a group 
     */
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query query = new Query("User")
        .setFilter(new FilterPredicate("userEmail", FilterOperator.EQUAL, userEmail));
    PreparedQuery results = datastore.prepare(query);
    if (results.countEntities() == 0) {
            Entity userEntity = new Entity("User");
            long timestamp = System.currentTimeMillis();
            userEntity.setProperty("userEmail", userEmail);
            userEntity.setProperty("timestamp", timestamp);
            datastore.put(userEntity);
        }
    }

    // User is logged in and has a nickname, so the request can proceed
    String logoutUrl = userService.createLogoutURL("/landing");
    loginStatus.logoutUrl = logoutUrl;
    String json = JsonUtility.convertToJsonUsingGson(loginStatus);
    out.println(json);

  }
}