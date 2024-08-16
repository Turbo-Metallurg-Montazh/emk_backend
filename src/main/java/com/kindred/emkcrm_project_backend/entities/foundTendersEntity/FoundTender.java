package com.kindred.emkcrm_project_backend.entities.foundTendersEntity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;

@Entity
@Table(name = "tenders")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FoundTender {

    @Column(name = "id")
    @Id
    @JsonProperty("Id")
    private String id;

    @Column(name = "notification_number")
    @JsonProperty("NotificationNumber")
    private String notificationNumber;

    @Column(name = "order_name")
    @JsonProperty("OrderName")
    private String orderName;

    @Column(name = "notification_type_desc")
    @JsonProperty("NotificationTypeDesc")
    private String notificationTypeDesc;

    @Column(name = "type_of_trading")
    @JsonProperty("TypeOfTrading")
    private int typeOfTrading;

    @Column(name = "max_price")
    @JsonProperty("MaxPrice")
    private double maxPrice;

    @Column(name = "currency")
    @JsonProperty("Currency")
    private String currency;

    @Column(name = "ep_uri")
    @JsonProperty("EpUri")
    private String epUri;

    @Column(name = "link")
    @JsonProperty("Link")
    private String link;

    @Column(name = "application_deadline")
    @JsonProperty("ApplicationDeadline")
    @Temporal(TemporalType.TIMESTAMP)
    private Date applicationDeadline;

    @Column(name = "is_cancelled")
    @JsonProperty("IsCancelled")
    private boolean isCancelled;

    @Column(name = "create_date")
    @JsonProperty("CreateDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;

    @Transient
    @JsonProperty("DeliveryPlaces")
    private ArrayList<String> deliveryPlaces;

    @Transient
    @JsonProperty("Inns")
    private ArrayList<String> inns;

    @Transient
    @JsonProperty("Participants")
    private ArrayList<Participant> participants;

    @Column(name = "other_information", columnDefinition = "TEXT")
    private String otherInformation;


    @Column(name = "commission_deadline")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonProperty("CommissionDeadline")
    private Date commissionDeadline;

    @Column(name = "is_abandoned")
    @JsonProperty("IsAbandoned")
    private boolean isAbandoned;

    @Column(name = "is_planning")
    @JsonProperty("IsPlanning")
    private boolean isPlanning;

}
