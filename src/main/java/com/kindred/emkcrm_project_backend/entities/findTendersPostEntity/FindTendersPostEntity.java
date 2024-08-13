package com.kindred.emkcrm_project_backend.entities.findTendersPostEntity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;

@Data
@AllArgsConstructor
public class FindTendersPostEntity {
    private ArrayList<String> text;
    private Boolean strictSearch;
    private Boolean attachments;
    private ArrayList<String> exclude;
    private ArrayList<String> regionIds;
    private ArrayList<Integer> categoryIds;
    private ArrayList<Integer> purchaseStatuses;
    private ArrayList<Integer> laws;
    private ArrayList<String> includeInns;
    private ArrayList<String> excludeInns;
    private ArrayList<Integer> procedures;
    private ArrayList<Integer> electronicPlaces;
    private Integer maxPriceFrom;
    private Integer maxPriceTo;
    private Boolean maxPriceNone;
    private Boolean advance44;
    private Boolean advance223;
    private Integer smp;
    private String dateTimeFrom;
    private String dateTimeTo;
    private int pageNumber;
}
