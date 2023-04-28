package com.example.warehouse.dto;

import com.example.warehouse.model.AssetType;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class AssetListViewDTO {
    private int id;
    private int userId;
    private String author;
    private String name;
    private String description;
    private byte[] image;
    private AssetType assetType;
    private LocalDate uploadDate;
}
