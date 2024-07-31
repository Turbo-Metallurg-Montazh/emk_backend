package com.kindred.emkcrm_project_backend.entities.tenderEntity.lots;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Okdp2 {
    @JsonProperty("Code")
    private String code;

    @JsonProperty("Name")
    private String name;
}
