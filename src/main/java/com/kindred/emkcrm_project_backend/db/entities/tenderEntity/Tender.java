package com.kindred.emkcrm_project_backend.db.entities.tenderEntity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.kindred.emkcrm_project_backend.db.entities.AuditableEntity;
import com.kindred.emkcrm_project_backend.db.entities.tenderEntity.lots.Lot;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Date;

@Entity
@Table(
        name = "purchase_tenders",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_purchase_tenders_purchase_source", columnNames = {"purchase_id", "source_type"})
        }
)
@Data
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Tender extends AuditableEntity {

    private static final ObjectMapper PAYLOAD_MAPPER = JsonMapper.builder().findAndAddModules().build();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    @EqualsAndHashCode.Include
    @Column(name = "db_id")
    private Long dbId;

    @JsonIgnore
    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", nullable = false, length = 16)
    private TenderSourceType sourceType = TenderSourceType.PROD;

    @Column(name = "purchase_id", nullable = false, length = 255)
    @JsonProperty("Id")
    private String id;

    @Column(name = "updated_datetime")
    @JsonProperty("UpdatedDatetime")
    private Date updatedDatetime;

    @Transient
    @JsonProperty("InitialSum")
    private InitialSum initialSum;

    @Column(name = "notification_type", length = 255)
    @JsonProperty("NotificationType")
    private String notificationType;

    @Column(name = "notification_placing_way", length = 255)
    @JsonProperty("NotificationPlacingWay")
    private String notificationPlacingWay;

    @Column(name = "auction_date_time")
    @JsonProperty("AuctionDateTime")
    private Date auctionDateTime;

    @Transient
    @JsonProperty("Docs")
    private ArrayList<Doc> docs;

    @Column(name = "etp_link", length = 2048)
    @JsonProperty("EtpLink")
    private String etpLink;

    @Column(name = "eis_link", length = 2048)
    @JsonProperty("EisLink")
    private String eisLink;

    @Column(name = "link", length = 2048)
    @JsonProperty("Link")
    private String link;

    @Transient
    @JsonProperty("Organizer")
    private Organizer organizer;

    @Column(name = "cancel_reason", columnDefinition = "TEXT")
    @JsonProperty("CancelReason")
    private String cancelReason;

    @Column(name = "planned_publish_date")
    @JsonProperty("PlannedPublishDate")
    private Date plannedPublishDate;

    @Column(name = "notification_number", length = 255)
    @JsonProperty("NotificationNumber")
    private String notificationNumber;

    @Column(name = "title", length = 1024)
    @JsonProperty("Title")
    private String title;

    @Column(name = "smp")
    @JsonProperty("SMP")
    private boolean smp;

    @Column(name = "publication_datetime_utc")
    @JsonProperty("PublicationDateTimeUTC")
    private Date publicationDateTimeUTC;

    @Column(name = "application_deadline")
    @JsonProperty("ApplicationDeadline")
    private Date applicationDeadline;

    @Column(name = "commission_deadline")
    @JsonProperty("CommissionDeadline")
    private Date commissionDeadline;

    @Transient
    @JsonProperty("ContactPerson")
    private ContactPerson contactPerson;

    @Transient
    @JsonProperty("Lots")
    private ArrayList<Lot> lots;

    @Column(name = "payload_json", nullable = false, columnDefinition = "TEXT")
    @JsonIgnore
    private String payloadJson;

    @PostLoad
    public void restoreTransientFields() {
        if (payloadJson == null || payloadJson.isBlank()) {
            return;
        }
        try {
            Tender payload = PAYLOAD_MAPPER.readValue(payloadJson, Tender.class);
            this.initialSum = payload.getInitialSum();
            this.docs = payload.getDocs();
            this.organizer = payload.getOrganizer();
            this.contactPerson = payload.getContactPerson();
            this.lots = payload.getLots();
        } catch (JsonProcessingException ignored) {
            // keep DB column values even if payload cannot be parsed
        }
    }

    @PrePersist
    @PreUpdate
    public void ensurePayload() {
        if (payloadJson != null && !payloadJson.isBlank()) {
            return;
        }
        try {
            this.payloadJson = PAYLOAD_MAPPER.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            this.payloadJson = "{}";
        }
    }
}
