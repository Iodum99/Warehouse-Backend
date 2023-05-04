package com.example.warehouse.dto;

import com.example.warehouse.model.AssetType;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class AssetDTO {

    private int id;
    private int userId;
    private String author;
    private String name;
    private String description;
    private String filePath;
    private List<String> imagePaths;
    private AssetType assetType;
    private LocalDate uploadDate;
    private LocalDate lastModifiedDate;
    private int downloads;
    private List<Integer> userIdLikes;
    private List<String> tags;
    private List<String> extensions;
    private String size;
}
