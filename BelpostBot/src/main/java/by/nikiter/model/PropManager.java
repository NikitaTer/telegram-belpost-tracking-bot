package by.nikiter.model;

import java.util.Locale;
import java.util.ResourceBundle;

public class PropManager {

    public static String getMessage(String key) {
        return ResourceBundle.getBundle("messages", Locale.forLanguageTag("ru")).getString(key);
    }

    public static String getData(String key) {
        return ResourceBundle.getBundle("data").getString(key);
    }
}
