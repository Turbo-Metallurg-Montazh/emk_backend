package com.kindred.emkcrm_project_backend.entities.tenderEntity.lots.customers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Customer {

    @JsonProperty("Organization")
    private Organization organization;

    @JsonProperty("CustomerRequirements")
    private CustomerRequirements customerRequirements;

    @JsonProperty("StartContractDate")
    private Date startContractDate;

    @JsonProperty("EndContractDate")
    private Date endContractDate;

    @JsonProperty("Objects")
    private ArrayList<CustomerObject> objects;
}
