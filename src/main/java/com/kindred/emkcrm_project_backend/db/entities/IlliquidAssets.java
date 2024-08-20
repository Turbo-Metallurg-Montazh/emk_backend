package com.kindred.emkcrm_project_backend.db.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;

@Entity
@Table(name = "illiquid_assets")

@Data
@NoArgsConstructor
public class IlliquidAssets {
    IlliquidAssets(String commodityMaterialValue, String articleNumber, float quantity, String unitsOfMeasurement, float price, String currency, String arrivalDate, String responsibleEmployee, long createdById, String commentary, String avito, String assetType, String assetStatus) {
        this.commodityMaterialValue = commodityMaterialValue;
        this.articleNumber = articleNumber;
        this.quantity = quantity;
        this.unitsOfMeasurement = unitsOfMeasurement;
        this.price = price;
        this.currency = currency;
        this.summaryPrice = price * quantity;
        this.arrivalDate = arrivalDate;
        this.responsibleEmployee = responsibleEmployee;
        this.createdById = createdById;
        this.commentary = commentary;
        this.avito = avito;
        this.assetType = assetType;
        this.assetStatus = assetStatus;
        this.creatingDate = LocalDateTime.now();

    }

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "commodity_material_value", nullable = false)
    private String commodityMaterialValue;

    @Column(name = "article_number")
    private String articleNumber;

    @Column(name = "quantity")
    private float quantity;

    @Column(name = "units_of_measurement")
    private String unitsOfMeasurement;

    @Column(name = "price")
    private float price;

    @Column(name = "currency")
    private String currency;

    @Column(name = "summary_price")
    private float summaryPrice;

    @Column(name = "arrival_date", nullable = false)
    private String arrivalDate;

    @Column(name = "creating_date", nullable = false)
    private LocalDateTime creatingDate;

    @Column(name = "last_update_date")
    private LocalDateTime lastUpdateDate;

    @Column(name = "responsible_employee")
    private String responsibleEmployee;

    @Column(name = "created_by_id", nullable = false)
    private long createdById;

    @Column(name = "commentary")
    private String commentary;

    @Column(name = "avito")
    private String avito;

    @Column(name = "asset_type")
    private String assetType;

    @Column(name = "asset_status", nullable = false)
    private String assetStatus;

}
