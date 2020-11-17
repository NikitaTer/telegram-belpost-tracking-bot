package by.nikiter.model.db.service;

import by.nikiter.model.UserState;
import by.nikiter.model.db.dao.StateDao;
import by.nikiter.model.db.dao.TrackingDao;
import by.nikiter.model.db.dao.UserDao;
import by.nikiter.model.db.entity.StateEntity;
import by.nikiter.model.db.entity.TrackingEntity;
import by.nikiter.model.db.entity.UserEntity;
import by.nikiter.model.db.entity.UserTrackingEntity;
import org.hibernate.Session;

import java.util.List;
import java.util.Set;

public class UserService {

    private final UserDao userDao;
    private final TrackingDao trackingDao;
    private final StateDao stateDao;

    public UserService(Session session) {
        userDao = new UserDao();
        trackingDao = new TrackingDao();
        stateDao = new StateDao();
        userDao.setSession(session);
        trackingDao.setSession(session);
        stateDao.setSession(session);
    }

    public boolean hasUser(String username) {
        return userDao.findById(username) != null;
    }

    public void addUser(String username, long chatId) {
        UserEntity user = userDao.findById(username);
        if (user == null) {
            user = new UserEntity();
            user.setUsername(username);
            user.setChatId(chatId);
            user.setState(stateDao.findById(UserState.USING_BOT.getCode()));
        }

        userDao.saveOrUpdate(user);
    }

    public void updateUser(UserEntity user) {
        userDao.merge(user);
    }

    public void deleteUser(String username) {
        userDao.deleteById(username);
    }

    public List<UserTrackingEntity> getAllTrackings(String username) {
        UserEntity user = userDao.findById(username);
        return user.getTrackings();
    }

    public boolean hasTracking(String username, String trackingNumber) {
        UserEntity user = userDao.findById(username);
        if (user == null) {
            return false;
        }

        for (UserTrackingEntity ute : user.getTrackings()) {
            if (ute.getTracking().getNumber().equals(trackingNumber)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasTrackings(String username) {
        UserEntity user = userDao.findById(username);
        if (user == null) {
            return false;
        }

        return user.getTrackings().size() > 0;
    }

    public String getTrackingName(String username, String trackingNumber) {
        UserEntity user = userDao.findById(username);
        if (user == null) {
            return null;
        }

        for (UserTrackingEntity ute : user.getTrackings()) {
            if (ute.getTracking().getNumber().equals(trackingNumber)) {
                return ute.getTrackingName();
            }
        }

        return null;
    }

    public StateEntity getUserState(String username) {
        UserEntity user = userDao.findById(username);
        return user == null ? null : user.getState();
    }

    public void changeUserState(String username, UserState state) {
        UserEntity user = userDao.findById(username);
        if (UserState.getEnum(user.getState().getName()) != state) {
            user.setState(stateDao.findById(state.getCode()));
            updateUser(user);
        }
    }

    public boolean addTracking(String username, String number, String name) {
        UserEntity user = userDao.findById(username);
        if (user == null) {
            return false;
        }
        TrackingEntity tracking = trackingDao.findById(number);
        if (tracking == null) {
            tracking = new TrackingEntity();
            tracking.setNumber(number);
        }
        user.addTracking(tracking, name);
        trackingDao.merge(tracking);
        return true;
    }

    public boolean removeTracking(String username, String number) {
        UserEntity user = userDao.findById(username);
        if (user == null) {
            return false;
        }

        TrackingEntity tracking = null;
        for (UserTrackingEntity currentTracking : user.getTrackings()) {
            if (currentTracking.getTracking().getNumber().equals(number)) {
                tracking = currentTracking.getTracking();
                break;
            }
        }

        if (tracking != null) {
            user.removeTracking(tracking);
            updateUser(user);
            return true;
        }
        return false;
    }
}
