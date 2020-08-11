package com.google.sps.servlets;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.sps.model.Cuisine;
import com.google.sps.model.Location;
import com.google.sps.model.Name;
import com.google.sps.model.Price;
import com.google.sps.model.Recommendation;
import com.google.sps.model.Restaurant;
import com.google.sps.model.User;
import com.google.sps.util.JsonUtility;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
        Price price = new Price(request.getParameter("price"));
        Cuisine cuisine = new Cuisine(request.getParameter("cuisine"));

        //Add new restaurant to datastore
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Entity recommendationEntity = new Entity("Recommendations");
        long timestamp = System.currentTimeMillis();
        recommendationEntity.setProperty("groupName", groupName); 
        recommendationEntity.setProperty("userEmail", userService.getCurrentUser().getEmail());
        recommendationEntity.setProperty("restaurantName", restaurantName.getName());
        recommendationEntity.setProperty("location", location.getLocation());
        recommendationEntity.setProperty("timestamp", timestamp);
        recommendationEntity.setProperty("price", price.getPrice());
        recommendationEntity.setProperty("cuisine", cuisine.getCuisine());
        datastore.put(recommendationEntity);

        // Redirect back to the HTML page.
        response.sendRedirect("/index.html");
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        
        String groupName = request.getParameter("groupName");
        String locationName = request.getParameter("locationName").toUpperCase();
        String priceName = request.getParameter("priceName");
        String cuisineName = request.getParameter("cuisineName");

        //Retrieve data from Datastore

        Collection<Filter> predicateList = new ArrayList<Filter>(Arrays.asList(
            new FilterPredicate("groupName", FilterOperator.EQUAL, groupName)
        ));

        if (!locationName.equals("ALL")) {
            predicateList.add(new FilterPredicate("location", FilterOperator.EQUAL, locationName));
        }
        if (!priceName.equals("All")) {
            predicateList.add(new FilterPredicate("price", FilterOperator.EQUAL, priceName));
        }
        if (!cuisineName.equals("All")) {
            predicateList.add(new FilterPredicate("cuisine", FilterOperator.EQUAL, cuisineName));
        }

        Query query = predicateList.size() == 1
            ? new Query("Recommendations")
                .setFilter(new FilterPredicate("groupName", FilterOperator.EQUAL, groupName))
                .addSort("timestamp", SortDirection.DESCENDING)
            : new Query("Recommendations")
                .setFilter(new CompositeFilter(
                    CompositeFilterOperator.AND,
                    predicateList   
                ))
                .addSort("timestamp", SortDirection.DESCENDING);

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery results = datastore.prepare(query);

        ArrayList<Recommendation> recommendations = new ArrayList();

        for (Entity entity : results.asIterable()) {
            String restaurantName = (String) entity.getProperty("restaurantName");
            String location = (String) entity.getProperty("location");
            String price = (String) entity.getProperty("price");
            String cuisine = (String) entity.getProperty("cuisine");
            Recommendation recommendation = new Recommendation(restaurantName, location, price, cuisine);
            recommendations.add(recommendation);
        }

        //Send Response
        response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().println(JsonUtility.convertToJsonUsingGson(recommendations));
    }
}