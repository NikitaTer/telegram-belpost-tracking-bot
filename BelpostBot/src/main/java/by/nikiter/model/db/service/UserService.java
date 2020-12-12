package by.nikiter.model.db.service;

import by.nikiter.model.UserState;
import by.nikiter.model.comparator.UserTrackingCreatedAtComparator;
import by.nikiter.model.comparator.UserTrackingNameComparator;
import by.nikiter.model.db.SessionManager;
import by.nikiter.model.db.SqlStatements;
import by.nikiter.model.db.dao.StateDao;
import by.nikiter.model.db.dao.TrackingDao;
import by.nikiter.model.db.dao.UserDao;
import by.nikiter.model.db.entity.StateEntity;
import by.nikiter.model.db.entity.TrackingEntity;
import by.nikiter.model.db.entity.UserEntity;
import by.nikiter.model.db.entity.UserTrackingEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.HibernateException;

import java.util.ArrayList;
import java.util.List;

/**
 * Service that provides an interface for working with user table through {@link UserDao}
 *
 * @see UserDao
 * @see TrackingDao
 * @see StateDao
 * @author NikiTer
 */
public class UserService {
    private static final Logger logger = LogManager.getLogger(UserService.class);

    private final UserDao userDao;
    private final TrackingDao trackingDao;
    private final StateDao stateDao;

    private final SessionManager sessionManager;

    public UserService(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
        userDao = new UserDao(sessionManager.getSession());
        trackingDao = new TrackingDao(sessionManager.getSession());
        stateDao = new StateDao(sessionManager.getSession());
    }

    public void addUser(String username, long chatId) {
        try {
            logger.info("Adding user " + username);
            if (!hasUser(username)) {
                sessionManager.beginTransaction();
                UserEntity user = new UserEntity();
                user.setUsername(username);
                user.setChatId(chatId);
                user.setState(stateDao.findById(UserState.USING_BOT.getCode()));
                userDao.saveOrUpdate(user);
                logger.info("User " + username + " added");
                sessionManager.commitTransaction();
            } else {
                logger.warn("User " + username + " already added");
            }
        } catch (RuntimeException rEx) {
            try {
                sessionManager.rollback();
            } catch (HibernateException hEx) {
                logger.error("Transaction cannot rollback: " + hEx);
            }
            sessionManager.closeSession();
            throw rEx;
        }
    }

    public boolean hasUser(String username) {
        logger.info("Checking if user " + username + " in database");
        UserEntity user = userDao.findById(username);
        if (user != null) {
            sessionManager.detach(user);
            return true;
        } else {
            return false;
        }
    }

    public void changeUserState(String username, UserState state) {
        try {
            UserEntity user = userDao.findById(username);
            if (UserState.getEnum(user.getState().getName()) != state) {
                logger.info("Changing state of user " + username);
                sessionManager.beginTransaction();
                user.setState(stateDao.findById(state.getCode()));
                sessionManager.flush();
                sessionManager.commitTransaction();
                logger.info("State of user " + username + " changed to " + state.getName());
            }
        } catch (RuntimeException rEx) {
            try {
                sessionManager.rollback();
            } catch (HibernateException hEx) {
                logger.error("Transaction cannot rollback: " + hEx);
            }
            sessionManager.closeSession();
            throw rEx;
        }
    }

    public StateEntity getUserState(String username) {
        logger.info("Getting state of user " + username);
        List<StateEntity> states = sessionManager.createQuery(SqlStatements.GET_USER_STATE, StateEntity.class)
                .setParameter("username", username)
                .getResultList();
        if (states.size() > 0) {
            return states.get(0);
        } else {
            logger.warn("User " + username + " have no state");
            return null;
        }
    }

    public void addTracking(String username, String number, String name) {
        try {
            logger.info("Adding tracking " + number + " with name + " + name + " to user " + username);
            UserEntity user = userDao.findById(username);
            if (user == null) {
                logger.warn("Can't find user " + username);
                return;
            }

            sessionManager.beginTransaction();
            TrackingEntity tracking = trackingDao.findById(number);
            if (tracking == null) {
                tracking = new TrackingEntity();
                tracking.setNumber(number);
                logger.info("Created new tracking " + number);
            }
            user.addTracking(tracking, name);
            trackingDao.merge(tracking);
            sessionManager.commitTransaction();
            logger.info("Added tracking " + number + " with name " + name + " to user " + username);
        } catch (RuntimeException rEx) {
            try {
                sessionManager.rollback();
            } catch (HibernateException hEx) {
                logger.error("Transaction cannot rollback: " + hEx);
            }
            sessionManager.closeSession();
            throw rEx;
        }
    }

    public boolean hasTracking(String username, String trackingNumber) {
        logger.info("Checking if user " + username + " has tracking " + trackingNumber);
        List<TrackingEntity> trackings = sessionManager.createQuery(SqlStatements.GET_TRACKING_BY_USER, TrackingEntity.class)
                .setParameter("username",username).setParameter("number",trackingNumber)
                .getResultList();
        trackings.forEach(sessionManager::detach);
        return trackings.size() > 0;
    }

    public boolean hasAnyTracking(String username) {
        logger.info("Checking if user " + username + " has any tracking");
        List<TrackingEntity> trackings = sessionManager.createQuery(SqlStatements.GET_ALL_TRACKINGS_BY_USER, TrackingEntity.class)
                .setParameter("username", username)
                .getResultList();
        trackings.forEach((sessionManager::detach));
        return trackings.size() > 0;
    }

    public String getTrackingName(String username, String trackingNumber) {
        logger.info("Getting name of tracking " + trackingNumber + " in " + username + " list");
        List<String> names = sessionManager.createQuery(SqlStatements.GET_TRACKING_NAME_BY_USER, String.class)
                .setParameter("username", username).setParameter("number", trackingNumber)
                .getResultList();
        if (names.size() > 0) {
            return names.get(0);
        } else {
            logger.warn("User " + username + " don't have tracking " + trackingNumber);
            return null;
        }
    }

    public List<String> getAllTrackingsNumbers(String username) {
        try {
            logger.info("Getting all trackings numbers of user " + username);
            List<String> numbers = null;
            UserEntity user = userDao.findById(username);
            if (user != null) {
                List<UserTrackingEntity> trackings = user.getTrackings();
                trackings.sort(new UserTrackingCreatedAtComparator().thenComparing(new UserTrackingNameComparator()));
                List<String> finalNumbers = new ArrayList<>();
                trackings.forEach((tr) -> finalNumbers.add(tr.getTracking().getNumber()));
                numbers = finalNumbers;
                sessionManager.clear();
            } else {
                logger.warn("Can't find user " + username);
            }
            return numbers;
        } catch (RuntimeException rEx) {
            sessionManager.closeSession();
            throw rEx;
        }
    }

    public List<String[]> getAllTrackingsNumbersAndNames(String username) {
        try {
            logger.info("Getting all trackings numbers and names of user " + username);
            List<String[]> numberNames = null;
            UserEntity user = userDao.findById(username);
            if (user != null) {
                List<UserTrackingEntity> trackings = user.getTrackings();
                numberNames = new ArrayList<String[]>();
                trackings.sort(new UserTrackingCreatedAtComparator().thenComparing(new UserTrackingNameComparator()));
                for (UserTrackingEntity ute : trackings) {
                    String[] numberName = new String[2];
                    numberName[0] = ute.getTracking().getNumber();
                    numberName[1] = ute.getTrackingName();
                    numberNames.add(numberName);
                }
                sessionManager.clear();
            } else {
                logger.warn("Can't find user " + username);
            }
            return numberNames;
        } catch (RuntimeException rEx) {
            sessionManager.closeSession();
            throw rEx;
        }
    }

    public boolean removeTracking(String username, String number) {
        try {
            UserEntity user = userDao.findById(username);
            if (user == null) {
                logger.warn("Can't find user " + username);
                return false;
            }

            logger.info("Removing tracking " + number + " from user " + username);
            sessionManager.beginTransaction();
            List<TrackingEntity> trackings = sessionManager
                    .createQuery(SqlStatements.GET_TRACKING_BY_USER, TrackingEntity.class)
                    .setParameter("username", username).setParameter("number", number)
                    .getResultList();

            if (trackings.size() > 0) {
                user.removeTracking(trackings.get(0));
                sessionManager.flush();
                sessionManager.commitTransaction();
                logger.info("Tracking + " + number + " removed from user " + username);
                return true;
            } else {
                logger.warn("Can't find tracking " + number + " in user " + username + " list");
            }
            sessionManager.commitTransaction();
            return false;
        } catch (RuntimeException rEx) {
            try {
                sessionManager.rollback();
            } catch (HibernateException hEx) {
                logger.error("Transaction cannot rollback: " + hEx);
            }
            sessionManager.closeSession();
            throw rEx;
        }
    }

    public void deleteUser(String username) {
        try {
            logger.info("Deleting user " + username);
            sessionManager.beginTransaction();
            userDao.deleteById(username);
            sessionManager.commitTransaction();
            logger.info("User " + username + " deleted");
        } catch (RuntimeException rEx) {
            try {
                sessionManager.rollback();
            } catch (HibernateException hEx) {
                logger.error("Transaction cannot rollback: " + hEx);
            }
            sessionManager.closeSession();
            throw rEx;
        }
    }
}
