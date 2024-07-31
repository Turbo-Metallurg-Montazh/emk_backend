package com.kindred.emkcrm_project_backend.entities.foundTendersEntity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FoundTender {

    @JsonProperty("Id")
    private String id;

    @JsonProperty("NotificationNumber")
    private String notificationNumber;

    @JsonProperty("OrderName")
    private String orderName;

    @JsonProperty("NotificationTypeDesc")
    private String notificationTypeDesc;

    @JsonProperty("TypeOfTrading")
    private int typeOfTrading;

    @JsonProperty("MaxPrice")
    private double maxPrice;

    @JsonProperty("Currency")
    private String currency;

    @JsonProperty("EpUri")
    private String epUri;

    @JsonProperty("Link")
    private String link;

    @JsonProperty("ApplicationDeadline")
    private Date applicationDeadline;

    @JsonProperty("IsCancelled")
    private boolean isCancelled;

    @JsonProperty("CreateDate")
    private Date createDate;

    @JsonProperty("DeliveryPlaces")
    private ArrayList<String> deliveryPlaces;

    @JsonProperty("Inns")
    private ArrayList<String> inns;

    @JsonProperty("Participants")
    private ArrayList<Participant> participants;

    @JsonProperty("CommissionDeadline")
    private Date commissionDeadline;

    @JsonProperty("IsAbandoned")
    private boolean isAbandoned;

    @JsonProperty("IsPlanning")
    private boolean isPlanning;


}
