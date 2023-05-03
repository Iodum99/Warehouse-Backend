package com.example.warehouse.dto;

import com.example.warehouse.model.AssetType;
import lombok.Data;

import java.util.List;

@Data
public class NewAssetDTO {

    private int userId;
    private String name;
    private String description;
    private AssetType assetType;
    private List<String> tags;
}
