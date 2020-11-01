package by.nikiter.model;

import by.nikiter.model.belpost.entity.Tracking;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jdk.nashorn.internal.parser.JSONParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParserJSON {

    private static volatile ParserJSON instance = null;

    private final ObjectMapper mapper;

    public static ParserJSON getInstance() {
        if (instance != null) {
            return instance;
        }

        synchronized (ParserJSON.class) {
            if (instance == null) {
                instance = new ParserJSON();
            }
            return instance;
        }
    }

    private ParserJSON() {
        mapper = new ObjectMapper();
    }

    public Tracking getTracking(String jsonString) {
        Tracking tracking = null;

        try {
            tracking = mapper.readValue(jsonString,Tracking.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return tracking;
    }
}
