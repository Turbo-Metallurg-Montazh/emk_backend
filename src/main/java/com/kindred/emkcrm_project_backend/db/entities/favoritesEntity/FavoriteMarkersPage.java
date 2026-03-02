package com.kindred.emkcrm_project_backend.db.entities.favoritesEntity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FavoriteMarkersPage {

    @JsonProperty("PageNumber")
    private Integer pageNumber;

    @JsonProperty("TotalCount")
    private Long totalCount;

    @JsonProperty("Items")
    private ArrayList<FavoriteMarker> items;
}
