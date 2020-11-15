package by.nikiter.model;

import by.nikiter.TgBot;
import by.nikiter.model.parser.ParserHTML;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class that is used to work with trackings and store data about them
 *
 * @author NikiTer
 */
public class PostTracker {

    private final Map<String, Timer> trackingTimerMap;

    private final static long UPDATE_DELAY = 3_600_000;

    private TgBot bot;

    private static volatile PostTracker instance = null;

    public static PostTracker getInstance() {
        if (instance != null) {
            return instance;
        }

        synchronized (PostTracker.class) {
            if (instance == null) {
                instance = new PostTracker();
            }
            return instance;
        }
    }

    private PostTracker() {

        userTrackingsMap = new HashMap<User, List<String>>();
        trackingLastEventMap = new HashMap<String, String>();
        trackingTimerMap = new HashMap<String, Timer>();
    }

    public void setBot(TgBot bot) {
        this.bot = bot;
    }

    public List<String> getAllTrackings(User user) {
        return userTrackingsMap.get(user);
    }

    public boolean hasTrackings(User user) {
        return userTrackingsMap.containsKey(user) && userTrackingsMap.get(user).size() != 0;
    }

    public boolean hasTracking(User user, String trackingNumber) {
        return userTrackingsMap.containsKey(user) && userTrackingsMap.get(user).contains(trackingNumber);
    }

    public boolean hasTracking(String trackingNumber) {
        boolean isContained = false;
        for (List<String> trs : userTrackingsMap.values()) {
            if (trs.contains(trackingNumber)) {
                isContained = true;
                break;
            }
        }
        return isContained;
    }

    public void addTracking(User user, String trackingNumber) {
        if (!userTrackingsMap.containsKey(user)) {
            userTrackingsMap.put(user,new ArrayList<>());
            userTrackingsMap.get(user).add(trackingNumber);
        } else if (!hasTracking(user, trackingNumber)) {
            userTrackingsMap.get(user).add(trackingNumber);
        }
    }

    public boolean deleteTracking(User user, String trackingNumber) {
        if (hasTracking(user,trackingNumber)) {
            userTrackingsMap.get(user).remove(trackingNumber);
            if (!hasTracking(trackingNumber)) {
                //stopUpdating(trackingNumber);
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Method that start regular updating tracking info about tracking
     *
     * @param trackingNum tracking number
     * @see PostTracker#updateTrackingInfo(String, String, boolean)
     * @see ParserHTML#getLastEvent(String)
     */
    public synchronized void startUpdating(String trackingNum) {

        Timer timer = new Timer(trackingNum + " Timer", true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateTrackingInfo(trackingNum, ParserHTML.getLastEvent(trackingNum), true);
            }
        }, UPDATE_DELAY, UPDATE_DELAY);

        if (!trackingTimerMap.containsKey(trackingNum)) {
            trackingTimerMap.put(trackingNum, timer);
        } else {
            trackingTimerMap.get(trackingNum).cancel();
            trackingTimerMap.replace(trackingNum,timer);
        }
    }

    /**
     * Method that cancel regular updating tracking info about tracking
     *
     * @param trackingNum tracking number
     */
    public synchronized void stopUpdating(String trackingNum) {
        if (trackingTimerMap.containsKey(trackingNum)) {
            trackingTimerMap.get(trackingNum).cancel();
            trackingTimerMap.remove(trackingNum);
            trackingLastEventMap.remove(trackingNum);
        }
    }

    /**
     * Method that update tracking info about tracking.
     *
     * @param trackingNum tracking number
     * @param lastEvent last event in tracking info
     * @param sendMessage Notify user about changes in his tracking if true
     */
    public void updateTrackingInfo(String trackingNum, String lastEvent, boolean sendMessage) {
        if (lastEvent == null) {
            return;
        }

        if (!trackingLastEventMap.containsKey(trackingNum)) {
            trackingLastEventMap.put(trackingNum,lastEvent);
        } else {
            if (!trackingLastEventMap.get(trackingNum).equals(lastEvent)) {
                trackingLastEventMap.replace(trackingNum,lastEvent);
                if (sendMessage) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(PropManager.getMessage("info_update").replaceAll("%num%", trackingNum)).append("\n");
                    sb.append(lastEvent);

                    getChatsIds(trackingNum).forEach((chatId) -> {
                        try {
                            bot.execute(new SendMessage(chatId, sb.toString()));
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        }
    }

    /**
     * Method that returns list of chats ids with users that tracking tracking (lol)
     * @param trackingNum tracking number
     * @return list of chats ids
     */
    private List<Long> getChatsIds(String trackingNum) {
        List<Long> ids = new ArrayList<>();

        userTrackingsMap.forEach((u,trs) -> {
            trs.forEach((tr) -> {
                if (trackingNum.equals(tr)) {
                    ids.add(UsersRep.getInstance().getChat(u).getId());
                }
            });
        });

        return ids;
    }
}