package com.example.warehouse.dto;

import com.example.warehouse.model.AssetType;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AssetDTO {

    private int id;
    private int userId;
    private String author;
    private String name;
    private String description;
    private String filePath;
    private byte[] image;
    private AssetType assetType;
    private LocalDate uploadDate;
    private LocalDate lastModifiedDate;
}
