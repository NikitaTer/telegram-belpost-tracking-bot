package by.nikiter.model;

/**
 * Enumeration of all states in which user can be
 *
 * @author NikiTer
 */
public enum UserState {
    USING_BOT(1,"USING_BOT", "User is using bot"),
    DELETING_TRACKING(2,"DELETING_TRACKING", "User choosing tracking to delete"),
    ENTERING_TRACKING_NUMBER(3,"ENTERING_TRACKING_NUMBER", "User is entering tracking number");

    private final int code;
    private final String name;
    private final String description;

    UserState(int code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    public static UserState getEnum (String name) {
        if (name == null) {
            return null;
        }

        try {
            return valueOf(name);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
