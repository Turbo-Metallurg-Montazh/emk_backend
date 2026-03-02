package com.kindred.emkcrm_project_backend.utils;

import com.kindred.emkcrm_project_backend.db.entities.IlliquidAssets;
import com.kindred.emkcrm_project_backend.db.repositories.IlliquidAssetsRepository;
import org.springframework.stereotype.Component;

@Component
public class AddIlliquidAsset {

    private final IlliquidAssetsRepository illiquidAssetsRepository;

    AddIlliquidAsset(IlliquidAssetsRepository illiquidAssetsRepository) {
        this.illiquidAssetsRepository = illiquidAssetsRepository;
    }

    public void addNewIlliquidAsset(IlliquidAssets illiquidAssets) {

        illiquidAssetsRepository.save(illiquidAssets);

    }

}
