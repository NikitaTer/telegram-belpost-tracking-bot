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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    private static final Logger logger = LogManager.getLogger(TrackingService.class);

    private final TrackingDao dao;

    private final SessionManager sessionManager;

    public TrackingService(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
        dao = new TrackingDao(sessionManager.getSession());
    }

    public Map<String, Timestamp> getAllTrackingsNumbersAndUpdatedAt() {
        try {
            logger.info("Getting all trackings numbers and updated at dates");
            Map<String, Timestamp> map = new HashMap<>();
            for (TrackingEntity tracking : dao.findAll()) {
                map.put(tracking.getNumber(), tracking.getUpdatedAt());
                sessionManager.detach(tracking);
            }
            return map;
        } catch (RuntimeException rEx) {
            sessionManager.closeSession();
            throw rEx;
        }
    }

    public boolean updateTrackingInfo(String trackingNumber, String lastEvent, String username) {
        try {
            logger.info("Updating info about tracking " + trackingNumber);
            TrackingEntity tracking = dao.findById(trackingNumber);
            if (tracking == null) {
                logger.warn("Can't find tracking " + trackingNumber);
                return false;
            }

            sessionManager.beginTransaction();
            tracking.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            if (!Objects.equals(tracking.getLastEvent(), lastEvent)) {
                tracking.setLastEvent(lastEvent);
                sessionManager.flush();
                sessionManager.commitTransaction();
                logger.info("Updated info about tracking " + trackingNumber);

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
                sessionManager.flush();
                sessionManager.commitTransaction();
                logger.info("Info about tracking " + trackingNumber + " was the same");
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
            logger.info("Trying to delete tracking " + number);
            TrackingEntity tracking = dao.findById(number);
            if (tracking != null && tracking.getUsers().size() == 0) {
                sessionManager.beginTransaction();
                dao.delete(tracking);
                sessionManager.commitTransaction();
                logger.info("Tracking " + number + " deleted");
                return true;
            } else {
                logger.info("Tracking " + number + " has more users");
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