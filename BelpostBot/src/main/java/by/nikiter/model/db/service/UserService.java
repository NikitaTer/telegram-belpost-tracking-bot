package by.nikiter.model.db.service;

import by.nikiter.model.UserState;
import by.nikiter.model.db.dao.StateDao;
import by.nikiter.model.db.dao.TrackingDao;
import by.nikiter.model.db.dao.UserDao;
import by.nikiter.model.db.entity.TrackingEntity;
import by.nikiter.model.db.entity.UserEntity;
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

    public UserEntity findByUsername(String username) {
        return userDao.findById(username);
    }

    public List<UserEntity> getAllUsers() {
        return userDao.findAll();
    }

    public void addUser(UserEntity user) {
        userDao.save(user);
    }

    public void deleteUser(String username) {
        userDao.deleteById(username);
    }

    public Set<TrackingEntity> getAllUsersTrackings(String username) {
        UserEntity user = userDao.findById(username);
        return (Set<TrackingEntity>) user.getTrackings();
    }

    public void changeUserState(String username, UserState state) {
        UserEntity user = userDao.findById(username);
        user.setState(stateDao.findById(state.getCode()));
        userDao.update(user);
    }

    public void addTracking(String username, String number, String name) {
        TrackingEntity tracking = new TrackingEntity();
        tracking.setNumber(number);
        tracking.setName(name);
        tracking.setUpdatable(true);
        UserEntity user = userDao.findById(username);
        user.addTracking(tracking);
        userDao.merge(user);
    }

    public void addTracking(String username, TrackingEntity tracking) {
        UserEntity user = userDao.findById(username);
        user.addTracking(tracking);
        userDao.merge(user);
    }

    public void removeTracking(String username, String number) {
        UserEntity user = userDao.findById(username);
        TrackingEntity tracking = null;
        for (TrackingEntity tr : user.getTrackings()) {
            if (tr.getNumber().equals(number)) {
                tracking = tr;
                break;
            }
        }

        if (tracking != null) {
            user.removeTracking(tracking);
            userDao.update(user);
        }
    }
}
