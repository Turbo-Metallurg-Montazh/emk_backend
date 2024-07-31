package com.kindred.emkcrm_project_backend.entities.foundTendersEntity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Participant {

    @JsonProperty("Inn")
    private String inn;

    @JsonProperty("IsWinner")
    private boolean isWinner;

    @JsonProperty("IsContractor")
    private boolean isContractor;

    @JsonProperty("FoundFromDocuments")
    private boolean foundFromDocuments;

}
