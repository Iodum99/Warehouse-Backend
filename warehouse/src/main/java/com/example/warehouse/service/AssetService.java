package com.example.warehouse.service;

import com.example.warehouse.dto.AssetDTO;
import com.example.warehouse.dto.AssetListViewDTO;
import com.example.warehouse.dto.NewAssetDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AssetService {

    void createAsset(NewAssetDTO newAsset, MultipartFile file, MultipartFile image, List<MultipartFile> gallery);
    AssetDTO findAssetById(int id);
    void updateAsset(AssetDTO asset, MultipartFile file, MultipartFile image, List<MultipartFile> gallery);
    void deleteAsset(int id);

    List<AssetListViewDTO> findAllAssets();
    List<AssetListViewDTO> findAllAssetsByUserId(int id);
}
