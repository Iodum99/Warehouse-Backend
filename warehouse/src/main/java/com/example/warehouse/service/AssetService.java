package com.example.warehouse.service;

import com.example.warehouse.dto.NewAssetDTO;
import org.springframework.web.multipart.MultipartFile;

public interface AssetService {

    void createAsset(NewAssetDTO newAsset, MultipartFile file, MultipartFile image);
}
