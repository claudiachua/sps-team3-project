package com.google.sps.util;

import com.google.gson.Gson;

/**
* Converts a ServerStats instance into a JSON string using the Gson library. Note: We first added
* the Gson library dependency to pom.xml.
*/
public class JsonUtility {
    // convert object to json string using gson
    public static <T> String convertToJsonUsingGson(T obj) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(obj);
        return jsonString;
    }
}
