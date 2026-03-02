package com.kindred.emkcrm_project_backend.db.entities.purchaseResultsEntity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.kindred.emkcrm_project_backend.db.entities.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

@Entity
@Table(
        name = "purchase_results",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_purchase_results_purchase_id", columnNames = {"purchase_id"})
        }
)
@Data
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PurchaseResult extends AuditableEntity {

    private static final ObjectMapper PAYLOAD_MAPPER = JsonMapper.builder().findAndAddModules().build();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @JsonIgnore
    @Column(name = "db_id")
    private Long dbId;

    @JsonProperty("Id")
    @Column(name = "purchase_id", nullable = false, length = 255)
    private String purchaseId;

    @JsonProperty("Link")
    @Column(name = "link", length = 2048)
    private String link;

    @Transient
    @JsonProperty("Protocols")
    private ArrayList<JsonNode> protocols;

    @Transient
    @JsonProperty("ContractProjects")
    private ArrayList<JsonNode> contractProjects;

    @Transient
    @JsonProperty("Contracts")
    private ArrayList<JsonNode> contracts;

    @Column(name = "protocols_count")
    private Integer protocolsCount;

    @Column(name = "contract_projects_count")
    private Integer contractProjectsCount;

    @Column(name = "contracts_count")
    private Integer contractsCount;

    @JsonIgnore
    @Column(name = "payload_json", nullable = false, columnDefinition = "TEXT")
    private String payloadJson;

    @PostLoad
    public void restoreTransientFields() {
        if (payloadJson == null || payloadJson.isBlank()) {
            return;
        }
        try {
            PurchaseResult payload = PAYLOAD_MAPPER.readValue(payloadJson, PurchaseResult.class);
            this.protocols = payload.getProtocols();
            this.contractProjects = payload.getContractProjects();
            this.contracts = payload.getContracts();
        } catch (JsonProcessingException ignored) {
            // keep DB values even if payload cannot be parsed
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
