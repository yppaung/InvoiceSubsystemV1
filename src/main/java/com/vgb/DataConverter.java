package com.vgb;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thoughtworks.xstream.XStream;

import java.util.Map;
import java.util.UUID;
/**
 * 
 * Converts data to JSON and XML files using gson and XML stream
 * Outputs to output folder
 * 
 */
public class DataConverter {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final XStream xStream = new XStream();

    static {
        xStream.alias("data", Map.class);
        xStream.alias("entry", Map.Entry.class);
    }

    public static String convertToJson(Map<UUID, ?> data) {
        return gson.toJson(data);
    }

    public static String convertToXml(Map<UUID, ?> data) {
        return xStream.toXML(data);
    }

}