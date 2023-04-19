package com.example.warehouse.dto;

import com.example.warehouse.model.AssetType;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.File;

@Data
public class NewAssetDTO {

    private int userId;
    private String name;
    private String description;
    private AssetType assetType;
    private byte[] image;

}
