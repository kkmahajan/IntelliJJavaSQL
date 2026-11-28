package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;
import java.util.Map;

public class Utils {

    // ObjectMapper and Gson are thread-safe and costly to build, so share one instance.
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final Gson GSON = new Gson();
    private static final Gson PRETTY_GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Convert a List<Map<String, Object>> to Json String
     * @param list as List<Map<String, Object>>
     * @return Json String
     * @throws IllegalStateException if the rows cannot be converted
     */
    public static String listOfMapToJsonStringUsingJackson(List<Map<String, Object>> list) {
        try {
            return OBJECT_MAPPER.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Unable to convert rows to JSON with Jackson", e);
        }
    }

    /**
     * Convert a List<Map<String, Object>> to Json String using ObjectMapper with default pretty printer
     * @param list as List<Map<String, Object>>
     * @return  Json String
     * @throws IllegalStateException if the rows cannot be converted
     */
    public static String listOfMapToPrettyJsonStringUsingJackson(List<Map<String, Object>> list) {
        try {
            return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(list);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Unable to convert rows to JSON with Jackson", e);
        }
    }

    /**
     * Convert a List<Map<String, Object>> to Json String using Gson
     * @param list as List<Map<String, Object>>
     * @return Json String
     */
    public static  String listOfMapToJsonStringUsingGson(List<Map<String, Object>> list){
        return GSON.toJson(list);
    }

    /**
     * Convert a List<Map<String, Object>> to Json String using Gson with default pretty printer
     * @param list as List<Map<String, Object>>
     * @return Json String
     */
    public static  String listOfMapToPrettyJsonStringUsingGson(List<Map<String, Object>> list){
        return PRETTY_GSON.toJson(list);
    }
}
