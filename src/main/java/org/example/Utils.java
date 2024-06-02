package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Utils {

    static final Logger LOGGER = Logger.getLogger(Utils.class.getName());

    /**
     * Convert a List<Map<String, Object>> to Json String
     * @param list as List<Map<String, Object>>
     * @return Json String
     */
    public static String listOfMapToJsonStringUsingJackson(List<Map<String, Object>> list) {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = null;
        try {
            jsonString = objectMapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            LOGGER.log(Level.WARNING, "JsonProcessingException while parsing the List<Map<String, Object>>", e);
        }
        return jsonString;
    }

    /**
     * Convert a List<Map<String, Object>> to Json String using ObjectMapper with default pretty printer
     * @param list as List<Map<String, Object>>
     * @return  Json String
     */
    public static String listOfMapToPrettyJsonStringUsingJackson(List<Map<String, Object>> list) {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = null;
        try {
            jsonString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(list);
        } catch (JsonProcessingException e) {
            LOGGER.log(Level.WARNING, "JsonProcessingException while parsing the List<Map<String, Object>>", e);
        }
        return jsonString;
    }

    /**
     * Convert a List<Map<String, Object>> to Json String using Gson
     * @param list as List<Map<String, Object>>
     * @return Json String
     */
    public static  String listOfMapToJsonStringUsingGson(List<Map<String, Object>> list){
        return new Gson().toJson(list);
    }

    /**
     * Convert a List<Map<String, Object>> to Json String using Gson with default pretty printer
     * @param list as List<Map<String, Object>>
     * @return Json String
     */
    public static  String listOfMapToPrettyJsonStringUsingGson(List<Map<String, Object>> list){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(list);
    }
}
