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
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.query.Query;

import javax.persistence.criteria.CriteriaQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Service that provides an interface for working with user table through {@link UserDao}
 *
 * @see UserDao
 * @see TrackingDao
 * @see StateDao
 * @author NikiTer
 */
public class UserService {

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
            sessionManager.beginTransaction();
            UserEntity user = userDao.findById(username);
            if (user == null) {
                user = new UserEntity();
                user.setUsername(username);
                user.setChatId(chatId);
                user.setState(stateDao.findById(UserState.USING_BOT.getCode()));
                userDao.saveOrUpdate(user);
            }
            sessionManager.commitTransaction();
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

    public boolean hasUser(String username) {
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
            sessionManager.beginTransaction();
            UserEntity user = userDao.findById(username);
            if (UserState.getEnum(user.getState().getName()) != state) {
                user.setState(stateDao.findById(state.getCode()));
                sessionManager.flush();
            }
            sessionManager.commitTransaction();
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

    public StateEntity getUserState(String username) {
        List<StateEntity> states = sessionManager.createQuery(SqlStatements.GET_USER_STATE, StateEntity.class)
                .setParameter("username", username)
                .getResultList();
        if (states.size() > 0) {
            return states.get(0);
        } else {
            return null;
        }
    }

    public void addTracking(String username, String number, String name) {
        try {
            sessionManager.beginTransaction();
            UserEntity user = userDao.findById(username);
            if (user == null) {
                sessionManager.commitTransaction();
                return;
            }

            TrackingEntity tracking = trackingDao.findById(number);
            if (tracking == null) {
                tracking = new TrackingEntity();
                tracking.setNumber(number);
            }
            user.addTracking(tracking, name);
            trackingDao.merge(tracking);
            sessionManager.commitTransaction();
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

    public boolean hasTracking(String username, String trackingNumber) {
        List<TrackingEntity> trackings = sessionManager.createQuery(SqlStatements.GET_TRACKING_BY_USER, TrackingEntity.class)
                .setParameter("username",username).setParameter("number",trackingNumber)
                .getResultList();
        trackings.forEach(sessionManager::detach);
        return trackings.size() > 0;
    }

    public boolean hasTrackings(String username) {
        List<TrackingEntity> trackings = sessionManager.createQuery(SqlStatements.GET_ALL_TRACKINGS_BY_USER, TrackingEntity.class)
                .setParameter("username", username)
                .getResultList();
        trackings.forEach((sessionManager::detach));
        return trackings.size() > 0;
    }

    public String getTrackingName(String username, String trackingNumber) {
        List<String> names = sessionManager.createQuery(SqlStatements.GET_TRACKING_NAME_BY_USER, String.class)
                .setParameter("username", username).setParameter("number", trackingNumber)
                .getResultList();
        if (names.size() > 0) {
            return names.get(0);
        } else {
            return null;
        }
    }

    public List<String> getAllTrackingsNumbers(String username) {
        try {
            sessionManager.beginTransaction();
            List<String> numbers = null;
            UserEntity user = userDao.findById(username);
            if (user != null) {
                List<UserTrackingEntity> trackings = user.getTrackings();
                trackings.sort(new UserTrackingCreatedAtComparator().thenComparing(new UserTrackingNameComparator()));
                List<String> finalNumbers = new ArrayList<>();
                trackings.forEach((tr) -> finalNumbers.add(tr.getTracking().getNumber()));
                numbers = finalNumbers;
                sessionManager.clear();
            }
            sessionManager.commitTransaction();
            return numbers;
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

    public List<String[]> getAllTrackingsNumbersAndNames(String username) {
        try {
            sessionManager.beginTransaction();
            List<String[]> pairs = null;
            UserEntity user = userDao.findById(username);
            if (user != null) {
                List<UserTrackingEntity> trackings = user.getTrackings();
                pairs = new ArrayList<String[]>();
                trackings.sort(new UserTrackingCreatedAtComparator().thenComparing(new UserTrackingNameComparator()));
                for (UserTrackingEntity ute : trackings) {
                    String[] pair = new String[2];
                    pair[0] = ute.getTracking().getNumber();
                    pair[1] = ute.getTrackingName();
                    pairs.add(pair);
                }
                sessionManager.clear();
            }
            sessionManager.commitTransaction();
            return pairs;
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

    public boolean removeTracking(String username, String number) {
        try {
            sessionManager.beginTransaction();
            UserEntity user = userDao.findById(username);
            if (user == null) {
                sessionManager.commitTransaction();
                return false;
            }

            List<TrackingEntity> trackings = sessionManager
                    .createQuery(SqlStatements.GET_TRACKING_BY_USER, TrackingEntity.class)
                    .setParameter("username", username).setParameter("number", number)
                    .getResultList();

            if (trackings.size() > 0) {
                user.removeTracking(trackings.get(0));
                sessionManager.flush();
                sessionManager.commitTransaction();
                return true;
            }
            sessionManager.commitTransaction();
            return false;
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

    public void deleteUser(String username) {
        try {
            sessionManager.beginTransaction();
            userDao.deleteById(username);
            sessionManager.commitTransaction();
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
