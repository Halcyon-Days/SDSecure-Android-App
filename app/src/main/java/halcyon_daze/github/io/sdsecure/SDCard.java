package halcyon_daze.github.io.sdsecure;

/**
 * Created by Chris on 2018-03-18.
 */

public class SDCard {
    private final String latitude;
    private final String longitude;
    private final String operation;
    private final String lastDayUpdated;
    private final String lastTimeUpdated;

    public SDCard(String latitude,String longitude, String operation, String lastTimeUpdated) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.operation = operation;
        this.lastDayUpdated = lastTimeUpdated.split("T")[0];
        this.lastTimeUpdated = lastTimeUpdated.split("T")[1];
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
}
