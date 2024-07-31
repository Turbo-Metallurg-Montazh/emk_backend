package com.kindred.emkcrm_project_backend.entities.tenderEntity.lots.customers;

import com.kindred.emkcrm_project_backend.entities.tenderEntity.lots.Ktru;
import com.kindred.emkcrm_project_backend.entities.tenderEntity.lots.Okdp2;
import com.kindred.emkcrm_project_backend.entities.tenderEntity.lots.Okved2;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerObject {

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Okpd2s")
    private ArrayList<Okdp2> okdp2s;

    @JsonProperty("Ktrus")
    private ArrayList<Ktru> ktrus;

    @JsonProperty("Okved2s")
    private ArrayList<Okved2> okved2s;

    @JsonProperty("Quantity")
    private double quantity;

    @JsonProperty("Okei")
    private String okei;

    @JsonProperty("ItemPrice")
    private double itemPrice;

    @JsonProperty("PositionPrice")
    private double positionPrice;
}
