package by.nikiter.model.db.service;

import by.nikiter.TgBot;
import by.nikiter.model.PropManager;
import by.nikiter.model.comparator.UserTrackingCreatedAtComparator;
import by.nikiter.model.comparator.UserTrackingNameComparator;
import by.nikiter.model.db.SessionManager;
import by.nikiter.model.db.dao.StateDao;
import by.nikiter.model.db.dao.TrackingDao;
import by.nikiter.model.db.entity.TrackingEntity;
import by.nikiter.model.db.entity.UserEntity;
import by.nikiter.model.db.entity.UserTrackingEntity;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.*;

/**
 * Service that provides an interface for working with state tracking through {@link TrackingDao}
 *
 * @see TrackingDao
 * @author NikiTer
 */
public class TrackingService {

    private final TrackingDao dao;

    private final SessionManager sessionManager;

    public TrackingService(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
        dao = new TrackingDao(sessionManager.getSession());
    }

    public Map<String, Timestamp> getAllTrackingsNumbersAndUpdatedAt() {
        try {
            sessionManager.beginTransaction();
            Map<String, Timestamp> map = new HashMap<>();
            for (TrackingEntity tracking : dao.findAll()) {
                map.put(tracking.getNumber(), tracking.getUpdatedAt());
                sessionManager.detach(tracking);
            }
            sessionManager.commitTransaction();
            return map;
        } catch (RuntimeException rEx) {
            try {
                sessionManager.rollback();
            } catch (HibernateException hEx) {
                hEx.printStackTrace();
            }
            sessionManager.closeSession();
            throw rEx;
        }
    }

    public boolean updateTrackingInfo(String trackingNumber, String lastEvent, String username) {
        try {
            sessionManager.beginTransaction();
            TrackingEntity tracking = dao.findById(trackingNumber);
            if (tracking == null) {
                sessionManager.commitTransaction();
                return false;
            }

            tracking.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            if (!Objects.equals(tracking.getLastEvent(), lastEvent)) {
                tracking.setLastEvent(lastEvent);
                //was dao.merge()
                sessionManager.flush();
                sessionManager.commitTransaction();
                String updateMessage = PropManager.getMessage("info_update");
                for (UserTrackingEntity ute : tracking.getUsers()) {
                    if (!ute.getUser().getUsername().equals(username)) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(updateMessage.replaceAll("%name%", ute.getTrackingName())).append("\n");
                        sb.append(lastEvent);
                        try {
                            TgBot.getInstance().execute(
                                    new SendMessage(ute.getUser().getChatId(), sb.toString()).enableHtml(true)
                            );
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return true;
            } else {
                //was dao.merge()
                sessionManager.flush();
                sessionManager.commitTransaction();
                return false;
            }
        } catch (RuntimeException rEx) {
            try {
                sessionManager.rollback();
            } catch (HibernateException hEx) {
                hEx.printStackTrace();
            }
            sessionManager.closeSession();
            throw rEx;
        }
    }

    public boolean updateTrackingInfo(String trackingNumber, String lastEvent) {
        return updateTrackingInfo(trackingNumber, lastEvent, null);
    }

    public boolean tryToDeleteTracking(String number) {
        try {
            sessionManager.beginTransaction();
            TrackingEntity tracking = dao.findById(number);
            if (tracking != null && tracking.getUsers().size() == 0) {
                dao.delete(tracking);
                sessionManager.commitTransaction();
                return true;
            } else {
                sessionManager.commitTransaction();
                return false;
            }
        } catch (RuntimeException rEx) {
            try {
                sessionManager.rollback();
            } catch (HibernateException hEx) {
                hEx.printStackTrace();
            }
            sessionManager.closeSession();
            throw rEx;
        }
    }
}