package by.nikiter.model;

/**
 * Enumeration of all states in which user can be
 *
 * @author NikiTer
 */
public enum UserState {
    USING_BOT("USING", "Using bot"),
    DELETING_TRACKING("DEL", "Deleting tracking"),
    ENTERING_TRACKING_NUMBER("ENT_NUM", "User is entering tracking number");

    private final String name;
    private final String description;

    UserState(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
