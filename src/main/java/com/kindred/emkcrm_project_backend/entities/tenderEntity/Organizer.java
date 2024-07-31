package com.kindred.emkcrm_project_backend.entities.tenderEntity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Organizer {
    @JsonProperty("FullName")
    private String fullName;

    @JsonProperty("Inn")
    private String inn;

    @JsonProperty("Kpp")
    private String kpp;
}
