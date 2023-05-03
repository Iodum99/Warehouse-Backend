package com.example.warehouse.service;

import com.example.warehouse.dto.AssetDTO;
import com.example.warehouse.dto.NewAssetDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AssetService {

    AssetDTO createAsset(NewAssetDTO newAsset, MultipartFile file, MultipartFile image, List<MultipartFile> gallery);
    AssetDTO findAssetById(int id);
    void updateAsset(AssetDTO asset, MultipartFile file, MultipartFile image, List<MultipartFile> gallery);
    void deleteAsset(int id);
    List<AssetDTO> findAllAssets();
    List<AssetDTO> findAllAssetsByUserIdAndAssetType(int id, String assetType);
    List<AssetDTO> findAllAssetsByType(String type);
    void increaseDownloadsCount(int id);
    void manageLikes(int assetId, int userId);
    List<AssetDTO> findFavoritesByUserId(int userId);

}
