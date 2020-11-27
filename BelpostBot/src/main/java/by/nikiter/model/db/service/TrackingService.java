package by.nikiter.model.db.service;

import by.nikiter.TgBot;
import by.nikiter.model.PropManager;
import by.nikiter.model.db.dao.StateDao;
import by.nikiter.model.db.dao.TrackingDao;
import by.nikiter.model.db.entity.TrackingEntity;
import by.nikiter.model.db.entity.UserTrackingEntity;
import org.hibernate.Session;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

/**
 * Service that provides an interface for working with state tracking through {@link TrackingDao}
 *
 * @see TrackingDao
 * @author NikiTer
 */
public class TrackingService {

    private final TrackingDao dao;

    public TrackingService(Session session) {
        dao = new TrackingDao();
        dao.setSession(session);
    }

    public TrackingEntity findByNumber(String number) {
        return dao.findById(number);
    }

    public List<TrackingEntity> getAllTrackings() {
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

    public boolean updateTrackingInfo(TrackingEntity tracking, String lastEvent, String username) {
        if (tracking == null) {
            return false;
        }

        tracking.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        if (!Objects.equals(tracking.getLastEvent(), lastEvent)) {
            tracking.setLastEvent(lastEvent);
            dao.merge(tracking);
            String updateMessage = PropManager.getMessage("info_update");
            for (UserTrackingEntity ute : tracking.getUsers()) {
                if (!ute.getUser().getUsername().equals(username)) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(updateMessage.replaceAll("%name%", ute.getTrackingName())).append("\n");
                    sb.append(lastEvent);
                    try {
                        TgBot.getInstance().execute(
                                new SendMessage(ute.getUser().getChatId(),sb.toString()).enableHtml(true)
                        );
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
            }
            return true;
        } else {
            dao.merge(tracking);
            return false;
        }
    }

    public boolean updateTrackingInfo(String trackingNumber, String lastEvent, String username) {
        return updateTrackingInfo(dao.findById(trackingNumber), lastEvent, username);
    }

    public boolean updateTrackingInfo(String trackingNumber, String lastEvent) {
        return updateTrackingInfo(dao.findById(trackingNumber), lastEvent, null);
    }

    public boolean updateTrackingInfo(TrackingEntity tracking, String lastEvent) {
        return updateTrackingInfo(tracking, lastEvent, null);
    }

    public boolean tryToDeleteTracking(String number) {
        TrackingEntity tracking = dao.findById(number);
        if (tracking != null && tracking.getUsers().size() == 0) {
            deleteTracking(tracking);
            return true;
        }
        return false;
    }

    public void tryToDeleteTrackings(List<String> numbers) {
        for (String number : numbers) {
            TrackingEntity tracking = dao.findById(number);
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