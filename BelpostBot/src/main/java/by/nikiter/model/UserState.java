package by.nikiter.model;

/**
 * Enumeration of all states in which user can be
 *
 * @author NikiTer
 */
public enum UserState {
    USING_BOT(1,"USING_BOT", "User is using bot"),
    ENTERING_TRACKING(2,"ENTERING_TRACKING", "User is entering tracking"),
    CHOOSING_TRACKING_TO_GET(3,"CHOOSING_TRACKING_TO_GET", "User is choosing tracking to get"),
    CHOOSING_TRACKING_TO_DELETE(4,"CHOOSING_TRACKING_TO_DELETE", "User is choosing tracking to delete");

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
