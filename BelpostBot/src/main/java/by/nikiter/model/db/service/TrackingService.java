package by.nikiter.model.db.service;

import by.nikiter.model.db.dao.TrackingDao;
import by.nikiter.model.db.entity.TrackingEntity;
import by.nikiter.model.db.entity.UserTrackingEntity;
import org.hibernate.Session;

import java.util.List;

public class TrackingService {

    private final TrackingDao dao;

    public TrackingService(Session session) {
        dao = new TrackingDao();
        dao.setSession(session);
    }

    public TrackingEntity findByNumber(String number) {
        return dao.findById(number);
    }

    public List<TrackingEntity> findAllTrackings() {
        return dao.findAll();
    }

    public void addTracking(TrackingEntity tracking) {
        dao.saveOrUpdate(tracking);
    }

    public void updateTracking(TrackingEntity tracking) {
        dao.merge(tracking);
    }

    public void deleteTracking(TrackingEntity tracking) {
        dao.delete(tracking);
    }

    public void tryToDeleteTracking(String number) {
        TrackingEntity tracking = dao.findById(number);
        if (tracking.getUsers().size() == 0) {
            deleteTracking(tracking);
        }
    }

    public void tryToDeleteTrackings(List<String> numbers) {
        for (String number : numbers) {
            TrackingEntity tracking = findByNumber(number);
            if (tracking.getUsers().size() == 0) {
                deleteTracking(tracking);
            }
        }
    }

    public List<UserTrackingEntity> getAllUsers(String number) {
        TrackingEntity tracking = dao.findById(number);
        return tracking.getUsers();
    }
}
