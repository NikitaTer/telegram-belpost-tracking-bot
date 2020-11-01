package by.nikiter.model.belpost.entity;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "ReferenceNumber",
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
public class OriginInfo {

    @JsonProperty("ReferenceNumber")
    private String referenceNumber;
    @JsonProperty("ItemReceived")
    private String itemReceived;
    @JsonProperty("ItemDispatched")
    private String itemDispatched;
    @JsonProperty("DepartfromAirport")
    private String departfromAirport;
    @JsonProperty("ArrivalfromAbroad")
    private String arrivalfromAbroad;
    @JsonProperty("CustomsClearance")
    private String customsClearance;
    @JsonProperty("DestinationArrived")
    private String destinationArrived;
    @JsonProperty("weblink")
    private String weblink;
    @JsonProperty("phone")
    private String phone;
    @JsonProperty("carrier_code")
    private String carrierCode;
    @JsonProperty("trackinfo")
    private List<Trackinfo> trackinfo = null;

    @JsonProperty("ReferenceNumber")
    public String getReferenceNumber() {
        return referenceNumber;
    }

    @JsonProperty("ReferenceNumber")
    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    @JsonProperty("ItemReceived")
    public String getItemReceived() {
        return itemReceived;
    }

    @JsonProperty("ItemReceived")
    public void setItemReceived(String itemReceived) {
        this.itemReceived = itemReceived;
    }

    @JsonProperty("ItemDispatched")
    public String getItemDispatched() {
        return itemDispatched;
    }

    @JsonProperty("ItemDispatched")
    public void setItemDispatched(String itemDispatched) {
        this.itemDispatched = itemDispatched;
    }

    @JsonProperty("DepartfromAirport")
    public String getDepartfromAirport() {
        return departfromAirport;
    }

    @JsonProperty("DepartfromAirport")
    public void setDepartfromAirport(String departfromAirport) {
        this.departfromAirport = departfromAirport;
    }

    @JsonProperty("ArrivalfromAbroad")
    public String getArrivalfromAbroad() {
        return arrivalfromAbroad;
    }

    @JsonProperty("ArrivalfromAbroad")
    public void setArrivalfromAbroad(String arrivalfromAbroad) {
        this.arrivalfromAbroad = arrivalfromAbroad;
    }

    @JsonProperty("CustomsClearance")
    public String getCustomsClearance() {
        return customsClearance;
    }

    @JsonProperty("CustomsClearance")
    public void setCustomsClearance(String customsClearance) {
        this.customsClearance = customsClearance;
    }

    @JsonProperty("DestinationArrived")
    public String getDestinationArrived() {
        return destinationArrived;
    }

    @JsonProperty("DestinationArrived")
    public void setDestinationArrived(String destinationArrived) {
        this.destinationArrived = destinationArrived;
    }

    @JsonProperty("weblink")
    public String getWeblink() {
        return weblink;
    }

    @JsonProperty("weblink")
    public void setWeblink(String weblink) {
        this.weblink = weblink;
    }

    @JsonProperty("phone")
    public String getPhone() {
        return phone;
    }

    @JsonProperty("phone")
    public void setPhone(String phone) {
        this.phone = phone;
    }

    @JsonProperty("carrier_code")
    public String getCarrierCode() {
        return carrierCode;
    }

    @JsonProperty("carrier_code")
    public void setCarrierCode(String carrierCode) {
        this.carrierCode = carrierCode;
    }

    @JsonProperty("trackinfo")
    public List<Trackinfo> getTrackinfo() {
        return trackinfo;
    }

    @JsonProperty("trackinfo")
    public void setTrackinfo(List<Trackinfo> trackinfo) {
        this.trackinfo = trackinfo;
    }

}
