package by.nikiter.model;

import by.nikiter.model.belpost.PostTracker;
import com.vdurmont.emoji.EmojiParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import sun.awt.Mutex;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ParserHTML {

    private final static long UPDATE_DELAY = 3_600_000;

    private static volatile ParserHTML instance = null;

    private final Map<String,Timer> trackingTimerMap;

    public static ParserHTML getInstance() {
        if (instance != null) {
            return instance;
        }

        synchronized (ParserHTML.class) {
            if (instance == null) {
                instance = new ParserHTML();
            }
            return instance;
        }
    }

    private ParserHTML() {
        trackingTimerMap = new HashMap<String, Timer>();
    }

    public synchronized String getTrackingMessage(String trackingNum) {
        StringBuilder sb = new StringBuilder();
        StringBuilder temp;

        try {
            Document doc = Jsoup.connect("https://webservices.belpost.by/searchRu/" + trackingNum)
                    .userAgent("Mozilla").get();
            Elements tables = doc.select("table");

            if (tables.size() < 3) {
                return PropManager.getMessage("tracking_message.html.no_info");
            }

            sb = new StringBuilder();
            String arrowDown = EmojiParser.parseToUnicode("         :arrow_down:\n");

            boolean isFirst = true;
            for (Element row : tables.get(0).select("tr")) {
                if (isFirst) {
                    isFirst = false;
                    continue;
                }
                sb.append("=======================").append("\n")
                        .append(PropManager.getMessage("tracking_message.html.date"))
                        .append(row.children().get(0).text()).append("\n")
                        .append(PropManager.getMessage("tracking_message.html.status"))
                        .append(row.children().get(1).text()).append("\n")
                        .append(PropManager.getMessage("tracking_message.html.office"))
                        .append(row.children().get(2).text()).append("\n")
                        .append("=======================").append("\n");
                if (
                        !row.equals(
                                tables.get(0).select("tr").get(tables.get(0).select("tr").size() - 1)
                        )
                ) {
                    sb.append(arrowDown).append(arrowDown).append(arrowDown).append(arrowDown);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    public synchronized void startUpdating(String trackingNum) {
        if (!trackingTimerMap.containsKey(trackingNum)) {
            Timer timer = new Timer(trackingNum + " Timer", true);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    PostTracker.getInstance().updateTrackingInfo(trackingNum,getLastEvent(trackingNum));
                }
            }, UPDATE_DELAY, UPDATE_DELAY);
            PostTracker.getInstance().updateTrackingInfo(trackingNum,getLastEvent(trackingNum));
            trackingTimerMap.put(trackingNum, timer);
        }
    }

    public synchronized void stopUpdating(String trackingNum) {
        if (trackingTimerMap.containsKey(trackingNum)) {
            trackingTimerMap.get(trackingNum).cancel();
            trackingTimerMap.remove(trackingNum);
        }
    }

    private synchronized String getLastEvent(String trackingNum) {
        StringBuilder sb = new StringBuilder();
        try {
            Document doc = Jsoup.connect("https://webservices.belpost.by/searchRu/" + trackingNum)
                    .userAgent("Mozilla").get();
            Elements tables = doc.select("table");


            if (tables.size() < 3) {
                return null;
            }

            Element lastRow = tables.get(0).select("tr").get(tables.get(0).select("tr").size() - 1);
            sb = new StringBuilder();

            sb.append("=======================").append("\n")
                    .append(PropManager.getMessage("tracking_message.html.date"))
                    .append(lastRow.children().get(0).text()).append("\n")
                    .append(PropManager.getMessage("tracking_message.html.status"))
                    .append(lastRow.children().get(1).text()).append("\n")
                    .append(PropManager.getMessage("tracking_message.html.office"))
                    .append(lastRow.children().get(2).text()).append("\n")
                    .append("=======================").append("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
