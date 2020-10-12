package by.nikiter.model.belpost;

import by.nikiter.model.ParserJSON;
import by.nikiter.model.PropManager;
import com.google.inject.internal.cglib.core.$Customizer;
import com.google.inject.internal.cglib.core.$MethodWrapper;
import org.telegram.telegrambots.meta.api.objects.User;

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
    private final Map<User, List<String>> trackers;
    private static final String belpost_code="belpochta";

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

        trackers = new HashMap<User, List<String>>();
    }

    public List<String> getAllTrackingNumbers(User user) {
        return trackers.get(user);
    }

    public String getAllTracking(User user, String language) {
        if (!trackers.containsKey(user)) {
            return "ERROR: У вас нет никаких трекеров";
        }

        StringBuilder trackingNumbers = new StringBuilder();

        for (String num : trackers.get(user)) {
            trackingNumbers.append(num).append(",");
        }
        trackingNumbers.deleteCharAt(trackingNumbers.length() - 1);

        String reqURL = "http://api.trackingmore.com/v2/trackings/get" +
                "?numbers=" + trackingNumbers.toString() +
                "&lang=" + language;
        return sendPost(reqURL,headParams,new ArrayList<String>(),"GET");
    }

    public int createTracking(User user, String trackingNumber, String language) {
        if (checkContainsTracker(user,trackingNumber)) {
            return 0;
        }

        String reqUrl="http://api.trackingmore.com/v2/trackings/post";
        List<String> bodyParams = new ArrayList<String>();

        String sb = "{\"tracking_number\": \"" + trackingNumber +
                "\",\"carrier_code\":\"" + belpost_code +
                "\",\"lang\":\"" + language + "\"}";
        bodyParams.add(sb);
        addTracker(user,trackingNumber);

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

    private void addTracker(User user, String trackingNumber) {
        if (!checkContainsUser(user)) {
            trackers.put(user,new ArrayList<>());
        }

        if (!checkContainsTracker(user, trackingNumber)) {
            trackers.get(user).add(trackingNumber);
        }
    }

    private boolean checkContainsTracker(User user, String trackingNumber) {
        return trackers.containsKey(user) && trackers.get(user).contains(trackingNumber);
    }

    private boolean checkContainsUser(User user) {
        return trackers.containsKey(user);
    }

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
