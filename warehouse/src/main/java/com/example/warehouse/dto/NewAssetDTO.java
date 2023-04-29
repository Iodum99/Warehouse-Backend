package com.example.warehouse.dto;

import com.example.warehouse.model.AssetType;
import lombok.Data;

@Data
public class NewAssetDTO {

    private int userId;
    private String name;
    private String description;
    private AssetType assetType;
}
