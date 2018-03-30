package halcyon_daze.github.io.sdsecure;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by Christopher on 2018-03-12.
 */

public class ServerComm {
    public static final String POST = "POST";
    public static final String DELETE = "DELETE";
    public static final String GET = "GET";
    private static final String URL = "http://192.168.1.65:5000/";
    // TODO: EHHHH
    public static final String URL_HISTORY = URL + "history?";
    public static final String URL_LOGIN = URL + "users?";
    public static final String URL_UPLOAD = URL + "upload";
    public static final String URL_HISTORY_LIST = URL + "history/user?";
    /*
     * Wrapper for send request, assuming url is constant
     */
    public static String getRequest (String requestType, HashMap<String, String> parameters, String url){
        return sendRequest(requestType, url, parameters);
    }

    public static String sendRequest (String requestType, String url, HashMap<String, String> parameters){
        String result = "";
        System.setProperty("http.keepAlive", "false");
        try {
            URL pageURL = new URL(addParams(url, parameters));

            System.out.println("Connecting to " + pageURL);

            HttpURLConnection urlConnection = (HttpURLConnection) pageURL.openConnection();
            urlConnection.setRequestMethod(requestType);
            urlConnection.setRequestProperty("Connection","Keep-Alive");

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            result = convertStreamToString(in);
            in.close();
            urlConnection.disconnect();
        } catch (IOException e) {
            result = "Request failed";
        }

        return result;
    }

    public static int uploadImage (File file, String username) {
        HttpClient httpClient = new DefaultHttpClient();
        try {
            //String filename = "guy.jpg";
            //File file = new File(Environment.getExternalStorageDirectory() + File.separator + filename);
            FileBody body = new FileBody(file);
            HttpPost request = new HttpPost(URL_UPLOAD);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();

            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            builder.addPart("file", body);
            builder.addPart("name", new StringBody(username));

            final HttpEntity entity = builder.build();
            request.setEntity(entity);
            HttpResponse response = httpClient.execute(request);

            return response.getStatusLine().getStatusCode();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
        return -1;
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
