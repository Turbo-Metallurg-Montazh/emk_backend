package com.kindred.emkcrm_project_backend.entities.tenderEntity.lots.customers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Guarantee {

    @JsonProperty("Amount")
    private double amount;

    @JsonProperty("Part")
    private double part;
}
