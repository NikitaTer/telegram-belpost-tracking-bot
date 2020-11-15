package by.nikiter.model;

import by.nikiter.model.db.entity.TrackingEntity;
import by.nikiter.model.db.service.ServiceManager;

import java.util.*;

/**
 * Class that is used to work with trackings and store data about them
 *
 * @author NikiTer
 */
public class TrackingUpdater {

    private final Map<String, Timer> trackingTimerMap;
    private final static long UPDATE_DELAY = 3_600_000;

    private static volatile TrackingUpdater instance = null;

    public static TrackingUpdater getInstance() {
        if (instance != null) {
            return instance;
        }

        synchronized (TrackingUpdater.class) {
            if (instance == null) {
                instance = new TrackingUpdater();
            }
            return instance;
        }
    }

    private TrackingUpdater() {
        trackingTimerMap = new HashMap<String, Timer>();
    }

    public void init() {
        ServiceManager manager = new ServiceManager();
        manager.openSession();
        for (TrackingEntity tracking : manager.getTrackingService().getAllTrackings()) {
            startOrRestartUpdate(tracking.getNumber());
        }
        manager.closeSession();
    }

    public synchronized void startOrRestartUpdate(String trackingNumber) {
        Timer timer = trackingTimerMap.put(trackingNumber, getTimer(trackingNumber));
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
    }

    public synchronized void stopUpdate(String trackingNumber) {
        Timer timer = trackingTimerMap.remove(trackingNumber);
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
    }

    public synchronized void stopAllUpdates() {
        for (Timer timer : trackingTimerMap.values()) {
            timer.cancel();
            timer.purge();
        }
    }

    private Timer getTimer(String trackingNumber) {
        Timer timer = new Timer(trackingNumber + " Timer", true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                String lastEvent = ParserHTML.getLastEvent(trackingNumber);
                if (lastEvent != null) {
                    ServiceManager sm = new ServiceManager();
                    sm.openSession();
                    sm.getTrackingService().updateTrackingInfo(trackingNumber, lastEvent);
                    sm.closeSession();
                }
            }
        },UPDATE_DELAY,UPDATE_DELAY);
        return timer;
    }
}