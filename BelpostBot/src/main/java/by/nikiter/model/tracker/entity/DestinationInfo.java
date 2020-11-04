package by.nikiter.model.tracker.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "ItemReceived",
        "ItemDispatched",
        "DepartfromAirport",
        "ArrivalfromAbroad",
        "CustomsClearance",
        "DestinationArrived",
        "weblink",
        "phone",
        "carrier_code",
        "trackinfo"
})
public class DestinationInfo {

    @JsonProperty("ItemReceived")
    private String itemReceived;
    @JsonProperty("ItemDispatched")
    private String itemDispatched;
    @JsonProperty("DepartfromAirport")
    private String departFromAirport;
    @JsonProperty("ArrivalfromAbroad")
    private String arrivalFromAbroad;
    @JsonProperty("CustomsClearance")
    private String customsClearance;
    @JsonProperty("DestinationArrived")
    private String destinationArrived;
    @JsonProperty("weblink")
    private String webLink;
    @JsonProperty("phone")
    private String phone;
    @JsonProperty("carrier_code")
    private String carrierCode;
    @JsonProperty("trackinfo")
    private Trackinfo trackInfo;

    @JsonProperty("ItemReceived")
    public Object getItemReceived() {
        return itemReceived;
    }

    @JsonProperty("ItemReceived")
    public void setItemReceived(String itemReceived) {
        this.itemReceived = itemReceived;
    }

    @JsonProperty("ItemDispatched")
    public Object getItemDispatched() {
        return itemDispatched;
    }

    @JsonProperty("ItemDispatched")
    public void setItemDispatched(String itemDispatched) {
        this.itemDispatched = itemDispatched;
    }

    @JsonProperty("DepartfromAirport")
    public Object getDepartFromAirport() {
        return departFromAirport;
    }

    @JsonProperty("DepartfromAirport")
    public void setDepartFromAirport(String departFromAirport) {
        this.departFromAirport = departFromAirport;
    }

    @JsonProperty("ArrivalfromAbroad")
    public Object getArrivalFromAbroad() {
        return arrivalFromAbroad;
    }

    @JsonProperty("ArrivalfromAbroad")
    public void setArrivalFromAbroad(String arrivalFromAbroad) {
        this.arrivalFromAbroad = arrivalFromAbroad;
    }

    @JsonProperty("CustomsClearance")
    public Object getCustomsClearance() {
        return customsClearance;
    }

    @JsonProperty("CustomsClearance")
    public void setCustomsClearance(String customsClearance) {
        this.customsClearance = customsClearance;
    }

    @JsonProperty("DestinationArrived")
    public Object getDestinationArrived() {
        return destinationArrived;
    }

    @JsonProperty("DestinationArrived")
    public void setDestinationArrived(String destinationArrived) {
        this.destinationArrived = destinationArrived;
    }

    @JsonProperty("weblink")
    public Object getWebLink() {
        return webLink;
    }

    @JsonProperty("weblink")
    public void setWebLink(String webLink) {
        this.webLink = webLink;
    }

    @JsonProperty("phone")
    public Object getPhone() {
        return phone;
    }

    @JsonProperty("phone")
    public void setPhone(String phone) {
        this.phone = phone;
    }

    @JsonProperty("carrier_code")
    public Object getCarrierCode() {
        return carrierCode;
    }

    @JsonProperty("carrier_code")
    public void setCarrierCode(String carrierCode) {
        this.carrierCode = carrierCode;
    }

    @JsonProperty("trackinfo")
    public Trackinfo getTrackInfo() {
        return trackInfo;
    }

    @JsonProperty("trackinfo")
    public void setTrackInfo(Trackinfo trackInfo) {
        this.trackInfo = trackInfo;
    }

}
