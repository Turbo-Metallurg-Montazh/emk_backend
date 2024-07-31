package com.kindred.emkcrm_project_backend.entities.tenderEntity.lots;

import com.kindred.emkcrm_project_backend.entities.tenderEntity.lots.customers.Customer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Lot {

    @JsonProperty("Title")
    private String title;

    @JsonProperty("Price")
    private Price price;

    @JsonProperty("Okdp2s")
    private ArrayList<Okdp2> okdp2s;

    @JsonProperty("Okved2s")
    private ArrayList<Okved2> okved2s;

    @JsonProperty("JointObjects")
    private ArrayList<JointObject> jointObjects;

    @JsonProperty("Customers")
    private ArrayList<Customer> customers;

    @JsonProperty("AbandonedReason")
    private String abandonedReason;

}
