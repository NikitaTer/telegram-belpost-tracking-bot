package by.nikiter.model.tracker.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "Date",
        "StatusDescription",
        "Details",
        "checkpoint_status",
        "substatus",
        "ItemNode"
})
public class Trackinfo {

    @JsonProperty("Date")
    private String date;
    @JsonProperty("StatusDescription")
    private String statusDescription;
    @JsonProperty("Details")
    private String details;
    @JsonProperty("checkpoint_status")
    private String checkpointStatus;
    @JsonProperty("substatus")
    private String substatus;
    @JsonProperty("ItemNode")
    private String itemNode;

    @JsonProperty("Date")
    public String getDate() {
        return date;
    }

    @JsonProperty("Date")
    public void setDate(String date) {
        this.date = date;
    }

    @JsonProperty("StatusDescription")
    public String getStatusDescription() {
        return statusDescription;
    }

    @JsonProperty("StatusDescription")
    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    @JsonProperty("Details")
    public String getDetails() {
        return details;
    }

    @JsonProperty("Details")
    public void setDetails(String details) {
        this.details = details;
    }

    @JsonProperty("checkpoint_status")
    public String getCheckpointStatus() {
        return checkpointStatus;
    }

    @JsonProperty("checkpoint_status")
    public void setCheckpointStatus(String checkpointStatus) {
        this.checkpointStatus = checkpointStatus;
    }

    @JsonProperty("substatus")
    public String getSubstatus() {
        return substatus;
    }

    @JsonProperty("substatus")
    public void setSubstatus(String substatus) {
        this.substatus = substatus;
    }

    @JsonProperty("ItemNode")
    public String getItemNode() {
        return itemNode;
    }

    @JsonProperty("ItemNode")
    public void setItemNode(String itemNode) {
        this.itemNode = itemNode;
    }

}
