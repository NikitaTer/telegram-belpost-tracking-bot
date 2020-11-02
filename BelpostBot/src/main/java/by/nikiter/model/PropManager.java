package by.nikiter.model;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Class that is used for access to properties
 *
 * @author NikiTer
 */
public class PropManager {

    /**
     * Method that returns value of message string
     * @param key name of string
     * @return value of string
     */
    public static String getMessage(String key) {
        return ResourceBundle.getBundle("messages", Locale.forLanguageTag("ru")).getString(key);
    }

    /**
     * Method that returns value of data string
     * @param key name of string
     * @return value of string
     */
    public static String getData(String key) {
        return ResourceBundle.getBundle("data").getString(key);
    }
}
