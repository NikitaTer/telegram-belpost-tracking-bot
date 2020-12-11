package by.nikiter.model;

import com.vdurmont.emoji.EmojiParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    private static final Logger logger = LogManager.getLogger(ParserHTML.class);

    private final static String URL = "https://webservices.belpost.by/searchRu/";
    private final static int TIMEOUT = 10_000;

    /**
     * Method that search for tracking info on belpost tracking site and returns result
     *
     * @param trackingNum tracking number
     * @return tracking info
     */
    public static String[] getTrackingMessage(String trackingNum) {
        logger.info("Parsing tracking message for tracking " + trackingNum);
        String[] result = new String[2];
        result[0] = null;
        result[1] = null;
        Elements tables;
        Document doc;

        try {
            doc = Jsoup.connect(URL + trackingNum)
                    .userAgent("Mozilla").timeout(TIMEOUT).get();
            tables = doc.select("table");
        } catch (IOException ex) {
            logger.error("Can't parse Belpost site for tracking " + trackingNum + ": " + ex.getMessage());
            return result;
        }

        if (tables.size() < 3) {
            result[0] = PropManager.getMessage("tracking_message.html.no_info");
            logger.info("No info about tracking " + trackingNum);
            return result;
        }

        logger.info("Building info message for tracking " + trackingNum);
        StringBuilder sb;
        StringBuilder temp;
        sb = new StringBuilder();
        String arrowDown = EmojiParser.parseToUnicode("         :arrow_down:\n");

        boolean isFirst = true;
        for (Element row : tables.get(0).select("tr")) {
            if (isFirst) {
                isFirst = false;
                continue;
            }
            temp = getBlock(row);
            sb.append(temp);
            if (
                    !row.equals(
                            tables.get(0).select("tr").get(tables.get(0).select("tr").size() - 1)
                    )
            ) {
                sb.append(arrowDown).append(arrowDown).append(arrowDown).append(arrowDown);
            } else {
                result[1] = temp.toString();
            }
        }
        result[0] = sb.toString();

        logger.info("Tracking message for tracking " + trackingNum + " built");
        return result;
    }

    /**
     * Method that search for tracking info on belpost tracking site and returns only last event from result
     *
     * @param trackingNum tracking number
     * @return last event from tracking info
     */
    public static String getLastEvent(String trackingNum) {
        logger.info("Parsing last event for tracking " + trackingNum);
        Document doc;
        Elements tables;
        try {
            doc = Jsoup.connect(URL + trackingNum)
                    .userAgent("Mozilla").timeout(TIMEOUT).get();
            tables = doc.select("table");
        } catch (IOException ex) {
            logger.error("Can't parse Belpost site for tracking " + trackingNum + ": " + ex.getMessage());
            return null;
        }

        if (tables.size() < 3) {
            logger.info("No last event for tracking " + trackingNum);
            return null;
        }

        Element lastRow = tables.get(0).select("tr").get(tables.get(0).select("tr").size() - 1);

        logger.info("Got last event for tracking " + trackingNum);
        return getBlock(lastRow).toString();
    }

    private static StringBuilder getBlock(Element row) {
        StringBuilder temp;
        temp = new StringBuilder();
        temp.append("=======================").append("\n")
                .append(PropManager.getMessage("tracking_message.html.date"))
                .append(row.children().get(0).text()).append("\n")
                .append(PropManager.getMessage("tracking_message.html.status"))
                .append(row.children().get(1).text()).append("\n")
                .append(PropManager.getMessage("tracking_message.html.office"))
                .append(row.children().get(2).text()).append("\n")
                .append("=======================").append("\n");
        return temp;
    }
}