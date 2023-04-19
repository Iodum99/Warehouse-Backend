package com.example.warehouse.dto;

import com.example.warehouse.model.AssetType;
import lombok.Data;

@Data
public class AssetDTO {

    private int id;
    private int userId;
    private String name;
    private String description;
    private String filePath;
    private AssetType assetType;
}
