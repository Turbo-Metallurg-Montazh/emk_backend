package com.kindred.emkcrm_project_backend.entities.foundTendersEntity;

import lombok.Data;

import java.util.Date;

@Data
public class FoundTendersArray {
    private Date fromDate;
    private Date toDate;
    private int tendersDownloadCount;
    private FoundTenders foundTenders;
}
