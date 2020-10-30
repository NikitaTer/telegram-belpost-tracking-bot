package by.nikiter.model.belpost;

import by.nikiter.TgBot;
import by.nikiter.model.PropManager;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PostTracker {

    private final Map<String, String> headParams;
    private final Map<User, List<String>> userTrackingsMap;
    //TODO: Перенести userChatMap в UsersRep
    private final Map<User, Chat> userChatMap;
    private final Map<String, String> trackingLastEventMap;

    private static final String belpost_code="belpochta";

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
        headParams = new HashMap<String, String>();
        headParams.put("Trackingmore-Api-Key", PropManager.getData("trackingmore.api"));
        headParams.put("Content-Type", "application/json");

        userTrackingsMap = new HashMap<User, List<String>>();
        trackingLastEventMap = new HashMap<String, String>();
        userChatMap = new HashMap<User, Chat>();
    }

    public void setBot(TgBot bot) {
        this.bot = bot;
    }

    public List<String> getAllTrackings(User user) {
        return userTrackingsMap.get(user);
    }

    public boolean hasTrackings(User user) {
        return userTrackingsMap.get(user) != null && userTrackingsMap.get(user).size() != 0;
    }

    /**
     * @deprecated
     */
    public String getAllTrackings(User user, String language) {
        if (!userTrackingsMap.containsKey(user)) {
            return "ERROR: У вас нет никаких трекеров";
        }

        StringBuilder trackingNumbers = new StringBuilder();

        for (String num : userTrackingsMap.get(user)) {
            trackingNumbers.append(num).append(",");
        }
        trackingNumbers.deleteCharAt(trackingNumbers.length() - 1);

        String reqURL = "http://api.trackingmore.com/v2/trackings/get" +
                "?numbers=" + trackingNumbers.toString() +
                "&lang=" + language;
        return sendPost(reqURL,headParams,new ArrayList<String>(),"GET");
    }

    /**
     * @deprecated
     */
    public int createTracking(User user, String trackingNumber, String language) {
        if (isContainsTracker(user,trackingNumber)) {
            return 4016;
        }

        String reqUrl="http://api.trackingmore.com/v2/trackings/post";
        List<String> bodyParams = new ArrayList<String>();

        String sb = "{\"tracking_number\": \"" + trackingNumber +
                "\",\"carrier_code\":\"" + belpost_code +
                "\",\"lang\":\"" + language + "\"}";
        bodyParams.add(sb);

        String result = sendPost(reqUrl,headParams,bodyParams,"POST");

        Matcher matcher = Pattern.compile("\"code\":[0-9]+,").matcher(result);
        if (matcher.find()) {
            result = result.substring(matcher.start(), matcher.end());

            matcher = Pattern.compile("[0-9]+").matcher(result);
            if (matcher.find()) {
                result = result.substring(matcher.start(),matcher.end());
                return Integer.parseInt(result);
            }
        }
        return -1;
    }

    public void addUserTracking(User user, String trackingNumber) {
        if (!hasUser(user)) {
            userTrackingsMap.put(user,new ArrayList<>());
            userTrackingsMap.get(user).add(trackingNumber);
        } else if (!isContainsTracker(user, trackingNumber)) {
            userTrackingsMap.get(user).add(trackingNumber);
        }
    }

    public boolean deleteTracking(User user, String trackingNumber) {
        if (isContainsTracker(user,trackingNumber)) {
            return userTrackingsMap.get(user).remove(trackingNumber);
        } else {
            return false;
        }
    }

    public void addUserChat(User user, Chat chat) {
        userChatMap.put(user,chat);
    }

    public boolean isContainsTracker(User user, String trackingNumber) {
        return userTrackingsMap.containsKey(user) && userTrackingsMap.get(user).contains(trackingNumber);
    }

    public boolean hasUser(User user) {
        return userTrackingsMap.containsKey(user);
    }

    public void updateTrackingInfo(String trackingNum, String lastEvent) {
        if (!trackingLastEventMap.containsKey(trackingNum)) {
            trackingLastEventMap.put(trackingNum,lastEvent);
        } else {
            if (!trackingLastEventMap.get(trackingNum).equals(lastEvent)) {
                trackingLastEventMap.replace(trackingNum,lastEvent);
                StringBuilder sb = new StringBuilder();
                sb.append(PropManager.getMessage("info_update").replaceAll("%num%",trackingNum)).append("\n");
                sb.append(lastEvent);

                getChatsIds(trackingNum).forEach((chatId) -> {
                    try {
                        bot.execute(new SendMessage(chatId,sb.toString()));
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }

    private List<Long> getChatsIds(String trackingNum) {
        List<Long> ids = new ArrayList<>();

        userTrackingsMap.forEach((u,trs) -> {
            trs.forEach((tr) -> {
                if (trackingNum.equals(tr)) {
                    ids.add(userChatMap.get(u).getId());
                }
            });
        });

        return ids;
    }

    /**
     * @deprecated
     */
    private String sendPost(String url, Map<String, String> headerParams , List<String> bodyParams,String mothod) {
        OutputStreamWriter out = null;
        BufferedReader in = null;
        StringBuilder result = new StringBuilder();
        try {
            URL realUrl = new URL(url);
            HttpURLConnection conn =(HttpURLConnection) realUrl.openConnection();

            conn.setDoOutput(true);
            conn.setDoInput(true);

            conn.setRequestMethod(mothod);

            for (Map.Entry<String, String> entry : headerParams.entrySet()) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
            conn.connect();

            out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");

            StringBuffer sbBody = new StringBuffer();
            for (String str : bodyParams) {
                sbBody.append(str);
            }
            out.write(sbBody.toString());

            out.flush();

            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result.toString();
    }

    /**
     * @deprecated
     */
    private String sendGet(String url, Map<String, String> headerParams,String mothod) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url;
            URL realUrl = new URL(urlNameString);

            HttpURLConnection connection =(HttpURLConnection) realUrl.openConnection();

            connection.setRequestMethod(mothod);

            for (Map.Entry<String, String> entry : headerParams.entrySet()) {
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }

            connection.connect();

            Map<String, List<String>> map = connection.getHeaderFields();

            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }

            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("Exception " + e);
            e.printStackTrace();
        }

        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }
}
