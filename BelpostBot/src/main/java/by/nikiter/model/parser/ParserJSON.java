package by.nikiter.model.parser;

import by.nikiter.model.tracker.entity.Tracking;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * JSON parser that works with tracking entity
 *
 * @author NikiTer
 */
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

    /**
     * Method that makes tracking entity out of json string
     *
     * @param jsonString json string
     * @return tracking entity
     */
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
