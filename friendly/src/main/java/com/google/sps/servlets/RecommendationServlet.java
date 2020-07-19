package com.google.sps.servlets;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.sps.model.Location;
import com.google.sps.model.Name;
import com.google.sps.model.Recommendation;
import com.google.sps.model.Restaurant;
import com.google.sps.model.User;
import com.google.sps.servlets.JsonUtility;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/recommendation")
public class RecommendationServlet extends HttpServlet {

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        UserService userService = UserServiceFactory.getUserService();

        // user not logged in, can not post recommendations
        if (!userService.isUserLoggedIn()){
            response.sendError(403, "Not authorized to make recommendations.");
            return;
        }

        String groupName = request.getParameter("groupName");
        Name restaurantName = new Name(request.getParameter("restaurantName"));      
        Location location = new Location(request.getParameter("location"));

        //Add new restaurant to datastore
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Entity reccomendationEntity = new Entity("Recommendations");
        long timestamp = System.currentTimeMillis();
        reccomendationEntity.setProperty("groupName", groupName); 
        reccomendationEntity.setProperty("userEmail", userService.getCurrentUser().getEmail());
        reccomendationEntity.setProperty("restaurantName", restaurantName.getName());
        reccomendationEntity.setProperty("location", location.getLocation());
        reccomendationEntity.setProperty("timestamp", timestamp);
        datastore.put(reccomendationEntity);

        // Redirect back to the HTML page.
        response.sendRedirect("/index.html");
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        
        String groupName = request.getParameter("groupName");
        String locationName = request.getParameter("locationName").toUpperCase();

        //Retrieve data from Datastore
        Query query = locationName.equals("ALL")
            ? new Query("Recommendations")
                .setFilter(new FilterPredicate("groupName", FilterOperator.EQUAL, groupName))
            : new Query("Recommendations")
                .setFilter(new CompositeFilter(
                    CompositeFilterOperator.AND,
                    Arrays.asList(
                        new FilterPredicate("groupName", FilterOperator.EQUAL, groupName),
                        new FilterPredicate("location", FilterOperator.EQUAL, locationName)
                    )    
                ));
            
            

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery results = datastore.prepare(query);

        ArrayList<Recommendation> recommendations = new ArrayList();

        for (Entity entity : results.asIterable()) {
            String restaurantName = (String) entity.getProperty("restaurantName");
            String location = (String) entity.getProperty("location");
            Recommendation recommendation = new Recommendation(restaurantName, location);
            recommendations.add(recommendation);
        }

        //Send Response
        response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().println(JsonUtility.convertToJsonUsingGson(recommendations));
    }
}