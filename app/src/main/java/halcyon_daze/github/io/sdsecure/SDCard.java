package halcyon_daze.github.io.sdsecure;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Chris on 2018-03-18.
 */

public class SDCard {
    private String latitude;
    private String longitude;
    private String operation;
    private String lastDayUpdated;
    private String lastTimeUpdated;
    private String id;

    public SDCard(String latitude,String longitude, String operation, String id, String lastTimeUpdated) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.operation = operation;
        this.id = id;
        this.lastDayUpdated = lastTimeUpdated.split("T")[0];
        this.lastTimeUpdated = lastTimeUpdated.split("T")[1];
    }

    public SDCard(JSONObject sdString) {
        try {
            this.latitude = sdString.get("lat").toString();
        } catch (JSONException e){
            this.latitude = "";
        }

        try {
            this.longitude = sdString.get("lng").toString();
        } catch (JSONException e){
            this.longitude = "";
        }

        try {
            this.id = sdString.get("id").toString();
        } catch (JSONException e){
            this.id = "";
        }

        try {
            this.operation = sdString.get("encryption").toString();
        } catch (JSONException e){
            this.operation= "";
        }

        try {
            this.lastDayUpdated = sdString.get("ts").toString().split("T")[0];
        } catch (JSONException e){
            this.lastDayUpdated = "";
        }

        try {
            this.lastTimeUpdated = sdString.get("ts").toString().split("T")[1];
        } catch (JSONException e){
            this.lastTimeUpdated = "";
        }

    }

    public static ArrayList<SDCard> parseSDJSON (JSONArray sdJSON) {
        ArrayList<SDCard> SDList = new ArrayList<SDCard>();

        for(int i = 0; i < sdJSON.length() ; i++) {
            try {
                SDList.add(new SDCard(sdJSON.getJSONObject(i)));
            } catch(JSONException e) {
            }
        }

        return SDList;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getOperation() {
        return operation;
    }

    public String getLastDayUpdated() {
        return lastDayUpdated;
    }

    public String getLastTimeUpdated() {
        return lastTimeUpdated;
    }

    public String getID() {
        return id;
    }

    public String toString() {
        return "lat = " + latitude + ", " +
                "lng = " + longitude + ", " +
                "op = " + operation + ", " +
                "id = " + id + ", " +
                "lday = " + lastDayUpdated + ", " +
                "ltime = " + lastTimeUpdated;
    }

    public static ArrayList<SDCard> testListCreate() {
        ArrayList<SDCard> cardList = new ArrayList<SDCard>();
        try {
            JSONArray testArray = new JSONArray("[{\"lat\": 49.2606, \"encryption\": 1, \"lng\": -123.2460, \"id\": 18, \"ts\": \"2018-03-13T00:33:04\"}," +
                    "{\"lat\": 49.4606, \"encryption\": 0, \"lng\": -123.0460, \"id\": 19, \"ts\": \"2018-03-13T00:34:04\"}," +
                    "{\"lat\": 49.5606, \"encryption\": 1, \"lng\": -123.5460, \"id\": 20, \"ts\": \"2018-03-13T00:35:04\"}]");
            cardList = SDCard.parseSDJSON(testArray);

        } catch(JSONException e) {
            System.out.println("Json parse failed");
        }
        return cardList;
    }
}
