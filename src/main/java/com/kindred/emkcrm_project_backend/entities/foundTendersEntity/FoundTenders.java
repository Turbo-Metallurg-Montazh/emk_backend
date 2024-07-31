package com.kindred.emkcrm_project_backend.entities.foundTendersEntity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FoundTenders {

    @JsonProperty("TotalCount")
    private int totalCount;

    @JsonProperty("Items")
    private ArrayList<FoundTender> foundTenders;
}
