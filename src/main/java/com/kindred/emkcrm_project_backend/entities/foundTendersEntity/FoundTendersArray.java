package com.kindred.emkcrm_project_backend.entities.foundTendersEntity;

import lombok.Data;

import java.util.Date;

import static com.kindred.emkcrm_project_backend.config.Constants.ITEMS_ON_PAGE;

@Data
public class FoundTendersArray {
    private Date fromDate;
    private Date toDate;
    private int tendersDownloadCount;
    private int totalPagesCount;
    private FoundTenders foundTenders;

    public void setTotalPagesCount(int totalTendersCount) {
        this.totalPagesCount = (int) Math.ceil((double) totalTendersCount / ITEMS_ON_PAGE);
    }
}
