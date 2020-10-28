package by.nikiter.model;

import com.vdurmont.emoji.EmojiParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class ParserHTML {

    public static String getTrackingMessage(String trackingNum) {
        StringBuilder sb = new StringBuilder();

        try {
            Document doc = Jsoup.connect("https://webservices.belpost.by/searchRu/" + trackingNum).userAgent("Mozilla").get();
            Elements tables = doc.select("table");

            if (tables.size() < 3) {
                return "ERROR";
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
}
