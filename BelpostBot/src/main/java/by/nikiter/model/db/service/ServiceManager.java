package by.nikiter.model.db.service;

import by.nikiter.model.db.SessionUtil;
import org.telegram.telegrambots.meta.api.objects.User;

public class ServiceManager {

    private UserService userService = null;
    private TrackingService trackingService = null;
    private StateService stateService = null;

    private final SessionUtil sessionUtil;

    public ServiceManager() {
        sessionUtil = new SessionUtil();
    }

    public void openSession() {
        sessionUtil.openSession();
    }

    public void closeSession() {
        sessionUtil.closeSession();
    }

    public UserService getUserService() {
        if (userService == null) {
            userService = new UserService(sessionUtil.getSession());
        }
        return userService;
    }

    public TrackingService getTrackingService() {
        if (trackingService == null) {
            trackingService = new TrackingService(sessionUtil.getSession());
        }
        return trackingService;
    }

    public StateService getStateService() {
        if (stateService == null) {
            stateService = new StateService(sessionUtil.getSession());
        }
        return stateService;
    }
}
