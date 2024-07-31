package com.kindred.emkcrm_project_backend.entities.tenderEntity.lots.customers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerRequirements {

    @JsonProperty("DeliveryPlaces")
    private ArrayList<String> DeliveryPlaces;

    @JsonProperty("ApplicationGuarantee")
    private double ApplicationGuarantee;

    @JsonProperty("ContractGuarantee")
    private Guarantee contractGuarantee;

    @JsonProperty("DeliveryGuarantee")
    private Guarantee deliveryGuarantee;

    @JsonProperty("ServiceGuarantee")
    private Guarantee serviceGuarantee;

    @JsonProperty("ProvisionWarranty")
    private double provisionWarranty;

    @JsonProperty("AdvancePayment")
    private double advancePayment;


}
