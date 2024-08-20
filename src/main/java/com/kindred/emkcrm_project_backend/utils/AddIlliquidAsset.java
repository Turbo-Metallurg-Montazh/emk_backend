package com.kindred.emkcrm_project_backend.utils;

import com.kindred.emkcrm_project_backend.db.entities.IlliquidAssets;
import com.kindred.emkcrm_project_backend.db.repositories.IlliquidAssetsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AddIlliquidAsset {
    @Autowired
    IlliquidAssetsRepository illiquidAssetsRepository;

    public void addNewIlliquidAsset(IlliquidAssets illiquidAssets) {

        illiquidAssetsRepository.save(illiquidAssets);

    }

}
