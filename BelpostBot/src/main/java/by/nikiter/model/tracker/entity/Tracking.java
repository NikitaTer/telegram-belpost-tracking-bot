package by.nikiter.model.belpost.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "meta",
        "data"
})
public class Tracking {

    @JsonProperty("meta")
    private TrackingMeta trackingMeta;
    @JsonProperty("data")
    private TrackingData trackingData;

    @JsonProperty("meta")
    public TrackingMeta getTrackingMeta() {
        return trackingMeta;
    }

    @JsonProperty("meta")
    public void setTrackingMeta(TrackingMeta trackingMeta) {
        this.trackingMeta = trackingMeta;
    }

    @JsonProperty("data")
    public TrackingData getTrackingData() {
        return trackingData;
    }

    @JsonProperty("data")
    public void setTrackingData(TrackingData trackingData) {
        this.trackingData = trackingData;
    }

}
