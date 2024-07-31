package com.kindred.emkcrm_project_backend.entities.tenderEntity.lots;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Price {
    @JsonProperty("Amount")
    private double amount;

    @JsonProperty("RubPrice")
    private double rubPrice;

    @JsonProperty("CurrencyCode")
    private String currencyCode;

    @JsonProperty("Nds")
    private String nds;
}
