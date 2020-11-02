package by.nikiter.model.tracker.entity;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "tracking_number",
        "carrier_code",
        "status",
        "track_update",
        "created_at",
        "updated_at",
        "order_create_time",
        "customer_email",
        "customer_phone",
        "title",
        "order_id",
        "comment",
        "customer_name",
        "archived",
        "original_country",
        "itemTimeLength",
        "stayTimeLength",
        "service_code",
        "status_info",
        "substatus",
        "origin_info",
        "destination_info",
        "lastEvent",
        "lastUpdateTime"
})
public class Item {

    @JsonProperty("id")
    private String id;
    @JsonProperty("tracking_number")
    private String trackingNumber;
    @JsonProperty("carrier_code")
    private String carrierCode;
    @JsonProperty("status")
    private String status;
    @JsonProperty("track_update")
    private Boolean trackUpdate;
    @JsonProperty("created_at")
    private String createdAt;
    @JsonProperty("updated_at")
    private String updatedAt;
    @JsonProperty("order_create_time")
    private String orderCreateTime;
    @JsonProperty("customer_email")
    private String customerEmail;
    @JsonProperty("customer_phone")
    private List<String> customerPhone = null;
    @JsonProperty("title")
    private String title;
    @JsonProperty("order_id")
    private String orderId;
    @JsonProperty("comment")
    private String comment;
    @JsonProperty("customer_name")
    private String customerName;
    @JsonProperty("archived")
    private Boolean archived;
    @JsonProperty("original_country")
    private String originalCountry;
    @JsonProperty("itemTimeLength")
    private String itemTimeLength;
    @JsonProperty("stayTimeLength")
    private String stayTimeLength;
    @JsonProperty("service_code")
    private String serviceCode;
    @JsonProperty("status_info")
    private String statusInfo;
    @JsonProperty("substatus")
    private String substatus;
    @JsonProperty("origin_info")
    private OriginInfo originInfo;
    @JsonProperty("destination_info")
    private DestinationInfo destinationInfo;
    @JsonProperty("lastEvent")
    private String lastEvent;
    @JsonProperty("lastUpdateTime")
    private String lastUpdateTime;

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("tracking_number")
    public String getTrackingNumber() {
        return trackingNumber;
    }

    @JsonProperty("tracking_number")
    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    @JsonProperty("carrier_code")
    public String getCarrierCode() {
        return carrierCode;
    }

    @JsonProperty("carrier_code")
    public void setCarrierCode(String carrierCode) {
        this.carrierCode = carrierCode;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("track_update")
    public Boolean getTrackUpdate() {
        return trackUpdate;
    }

    @JsonProperty("track_update")
    public void setTrackUpdate(Boolean trackUpdate) {
        this.trackUpdate = trackUpdate;
    }

    @JsonProperty("created_at")
    public String getCreatedAt() {
        return createdAt;
    }

    @JsonProperty("created_at")
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @JsonProperty("updated_at")
    public String getUpdatedAt() {
        return updatedAt;
    }

    @JsonProperty("updated_at")
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    @JsonProperty("order_create_time")
    public String getOrderCreateTime() {
        return orderCreateTime;
    }

    @JsonProperty("order_create_time")
    public void setOrderCreateTime(String orderCreateTime) {
        this.orderCreateTime = orderCreateTime;
    }

    @JsonProperty("customer_email")
    public String getCustomerEmail() {
        return customerEmail;
    }

    @JsonProperty("customer_email")
    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    @JsonProperty("customer_phone")
    public List<String> getCustomerPhone() {
        return customerPhone;
    }

    @JsonProperty("customer_phone")
    public void setCustomerPhone(List<String> customerPhone) {
        this.customerPhone = customerPhone;
    }

    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    @JsonProperty("title")
    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty("order_id")
    public String getOrderId() {
        return orderId;
    }

    @JsonProperty("order_id")
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    @JsonProperty("comment")
    public String getComment() {
        return comment;
    }

    @JsonProperty("comment")
    public void setComment(String comment) {
        this.comment = comment;
    }

    @JsonProperty("customer_name")
    public String getCustomerName() {
        return customerName;
    }

    @JsonProperty("customer_name")
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    @JsonProperty("archived")
    public Boolean getArchived() {
        return archived;
    }

    @JsonProperty("archived")
    public void setArchived(Boolean archived) {
        this.archived = archived;
    }

    @JsonProperty("original_country")
    public String getOriginalCountry() {
        return originalCountry;
    }

    @JsonProperty("original_country")
    public void setOriginalCountry(String originalCountry) {
        this.originalCountry = originalCountry;
    }

    @JsonProperty("itemTimeLength")
    public String getItemTimeLength() {
        return itemTimeLength;
    }

    @JsonProperty("itemTimeLength")
    public void setItemTimeLength(String itemTimeLength) {
        this.itemTimeLength = itemTimeLength;
    }

    @JsonProperty("stayTimeLength")
    public String getStayTimeLength() {
        return stayTimeLength;
    }

    @JsonProperty("stayTimeLength")
    public void setStayTimeLength(String stayTimeLength) {
        this.stayTimeLength = stayTimeLength;
    }

    @JsonProperty("service_code")
    public String getServiceCode() {
        return serviceCode;
    }

    @JsonProperty("service_code")
    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    @JsonProperty("status_info")
    public String getStatusInfo() {
        return statusInfo;
    }

    @JsonProperty("status_info")
    public void setStatusInfo(String statusInfo) {
        this.statusInfo = statusInfo;
    }

    @JsonProperty("substatus")
    public String getSubstatus() {
        return substatus;
    }

    @JsonProperty("substatus")
    public void setSubstatus(String substatus) {
        this.substatus = substatus;
    }

    @JsonProperty("origin_info")
    public OriginInfo getOriginInfo() {
        return originInfo;
    }

    @JsonProperty("origin_info")
    public void setOriginInfo(OriginInfo originInfo) {
        this.originInfo = originInfo;
    }

    @JsonProperty("destination_info")
    public DestinationInfo getDestinationInfo() {
        return destinationInfo;
    }

    @JsonProperty("destination_info")
    public void setDestinationInfo(DestinationInfo destinationInfo) {
        this.destinationInfo = destinationInfo;
    }

    @JsonProperty("lastEvent")
    public String getLastEvent() {
        return lastEvent;
    }

    @JsonProperty("lastEvent")
    public void setLastEvent(String lastEvent) {
        this.lastEvent = lastEvent;
    }

    @JsonProperty("lastUpdateTime")
    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    @JsonProperty("lastUpdateTime")
    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

}
