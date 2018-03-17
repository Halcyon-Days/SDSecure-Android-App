package halcyon_daze.github.io.sdsecure;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.net.URL;

/**
 * Created by Christopher on 2018-03-12.
 */

public class ServerComm {
    public static final String POST = "POST";
    public static final String DELETE = "DELETE";
    public static final String GET = "GET";
    private static final String URL = "http://pk080596pi.ddns.net:5000/history?";

    /*
     * Wrapper for send request, assuming url is constant
     */
    public static String getRequest (String requestType, HashMap<String, String> parameters){
        return sendRequest(requestType, URL, parameters);
    }

    public static String sendRequest (String requestType, String url, HashMap<String, String> parameters){
        String result = "";

        try {
            URL pageURL = new URL(addParams(url, parameters));

            System.out.println("Connecting to " + pageURL);

            HttpURLConnection urlConnection = (HttpURLConnection) pageURL.openConnection();
            urlConnection.setRequestMethod(requestType);

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            result = convertStreamToString(in);

        } catch (IOException e) {
            result = "Request failed";
        }

        return result;
    }

    private static String convertStreamToString (InputStream in) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder();

        String input;

        try {
            while ((input = reader.readLine()) != null) {
                sb.append(input).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }

    public static String addParams(String url, HashMap<String, String> parameters) {
        int i = 0;

        for (String key : parameters.keySet()) {
            url += key + "=" + parameters.get(key);

            if (++i < parameters.keySet().size()) {
                url += "&";
            }
        }

        return url;
    }
}
