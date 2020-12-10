package by.nikiter.model.db.service;

import by.nikiter.model.db.SessionManager;

/**
 * Class that manages all services and hibernate sessions
 *
 * @see UserService
 * @see TrackingService
 * @see SessionManager
 * @author NikiTer
 */
public class ServiceManager {

    private UserService userService = null;
    private TrackingService trackingService = null;

    private final SessionManager sessionManager;

    public ServiceManager() {
        sessionManager = new SessionManager();
    }

    public void openSession() {
        sessionManager.openSession();
    }

    public void closeSession() {
        sessionManager.closeSession();
    }

    public UserService getUserService() {
        if (userService == null) {
            userService = new UserService(sessionManager);
        }
        return userService;
    }

    public TrackingService getTrackingService() {
        if (trackingService == null) {
            trackingService = new TrackingService(sessionManager);
        }
        return trackingService;
    }
}
