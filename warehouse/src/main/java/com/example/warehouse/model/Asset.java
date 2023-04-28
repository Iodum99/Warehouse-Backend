package com.example.warehouse.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "asset_table")
@Data
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String author;
    private String name;
    private String description;
    private String filePath;
    private byte[] image;
    @ElementCollection
    private List<byte[]> gallery;
    private AssetType assetType;
    private LocalDate uploadDate;
    private LocalDate lastModifiedDate;
}
