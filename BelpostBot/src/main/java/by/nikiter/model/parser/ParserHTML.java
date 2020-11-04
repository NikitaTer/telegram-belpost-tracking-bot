package by.nikiter.model.parser;

import by.nikiter.model.PropManager;
import by.nikiter.model.tracker.PostTracker;
import com.vdurmont.emoji.EmojiParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.SocketTimeoutException;

/**
 * HTML Parser that parse <a href="https://webservices.belpost.by/searchRu">Belpost tracking site</a>
 *
 * @author NikiTer
 */
public class ParserHTML {

    private final static String URL = "https://webservices.belpost.by/searchRu/";
    private final static int TIMEOUT = 10_000;

    /**
     * Method that search for tracking info on belpost tracking site and returns result
     *
     * @param trackingNum tracking number
     * @return tracking info
     */
    public static synchronized String getTrackingMessage(String trackingNum) {
        StringBuilder sb = new StringBuilder();
        StringBuilder temp;

        try {
            Document doc = Jsoup.connect(URL + trackingNum)
                    .userAgent("Mozilla").timeout(TIMEOUT).get();
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
                temp = new StringBuilder();
                temp.append("=======================").append("\n")
                        .append(PropManager.getMessage("tracking_message.html.date"))
                        .append(row.children().get(0).text()).append("\n")
                        .append(PropManager.getMessage("tracking_message.html.status"))
                        .append(row.children().get(1).text()).append("\n")
                        .append(PropManager.getMessage("tracking_message.html.office"))
                        .append(row.children().get(2).text()).append("\n")
                        .append("=======================").append("\n");
                sb.append(temp);
                if (
                        !row.equals(
                                tables.get(0).select("tr").get(tables.get(0).select("tr").size() - 1)
                        )
                ) {
                    sb.append(arrowDown).append(arrowDown).append(arrowDown).append(arrowDown);
                } else {
                    PostTracker.getInstance().updateTrackingInfo(trackingNum,temp.toString(),false);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            if (e instanceof SocketTimeoutException) {
                return null;
            }
        }

        return sb.toString();
    }

    /**
     * Method that search for tracking info on belpost tracking site and returns only last event from result
     *
     * @param trackingNum tracking number
     * @return last event from tracking info
     */
    public static synchronized  String getLastEvent(String trackingNum) {
        StringBuilder sb = new StringBuilder();
        try {
            Document doc = Jsoup.connect(URL + trackingNum)
                    .userAgent("Mozilla").timeout(TIMEOUT).get();
            Elements tables = doc.select("table");


            if (tables.size() < 3) {
                return PropManager.getMessage("tracking_message.html.no_info");
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
            if (e instanceof SocketTimeoutException) {
                return null;
            }
        }
        return sb.toString();
    }
}