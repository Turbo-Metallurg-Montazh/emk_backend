package com.kindred.emkcrm_project_backend.entities.tenderEntity;
import com.kindred.emkcrm_project_backend.entities.tenderEntity.lots.Lot;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Tender {
    @JsonProperty("Id")
    private String id;

    @JsonProperty("UpdatedDatetime")
    private Date updatedDatetime;

    @JsonProperty("InitialSum")
    private InitialSum initialSum;

    @JsonProperty("NotificationType")
    private String notificationType;

    @JsonProperty("NotificationPlacingWay")
    private String notificationPlacingWay;

    @JsonProperty("AuctionDateTime")
    private Date auctionDateTime;

    @JsonProperty("Docs")
    private ArrayList<Doc> docs;

    @JsonProperty("EtpLink")
    private String etpLink;

    @JsonProperty("EisLink")
    private String eisLink;

    @JsonProperty("Link")
    private String link;

    @JsonProperty("Organizer")
    private Organizer organizer;

    @JsonProperty("CancelReason")
    private String cancelReason;

    @JsonProperty("PlannedPublishDate")
    private Date plannedPublishDate;

    @JsonProperty("NotificationNumber")
    private String notificationNumber;

    @JsonProperty("Title")
    private String title;

    @JsonProperty("SMP")
    private boolean smp;

    @JsonProperty("PublicationDateTimeUTC")
    private Date publicationDateTimeUTC;

    @JsonProperty("ApplicationDeadline")
    private Date applicationDeadline;

    @JsonProperty("CommissionDeadline")
    private Date commissionDeadline;

    @JsonProperty("ContactPerson")
    private ContactPerson contactPerson;

    @JsonProperty("Lots")
    private ArrayList<Lot> lots;
}
