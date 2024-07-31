package com.kindred.emkcrm_project_backend.entities.tenderEntity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class InitialSum {

    @JsonProperty("Price")
    private double price;

    @JsonProperty("Currency")
    private String currency;
}
