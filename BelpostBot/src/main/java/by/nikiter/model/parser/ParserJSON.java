package by.nikiter.model.parser;

import by.nikiter.model.tracker.entity.Tracking;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
