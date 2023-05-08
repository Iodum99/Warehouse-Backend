package com.example.warehouse.service;

import com.example.warehouse.dto.AssetDTO;
import com.example.warehouse.dto.FilterDataDTO;
import com.example.warehouse.dto.NewAssetDTO;
import com.example.warehouse.model.AssetType;
import com.example.warehouse.model.helper.AssetSpecification;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface AssetService {

    AssetDTO createAsset(NewAssetDTO newAsset, MultipartFile file, MultipartFile image, List<MultipartFile> gallery) throws IOException;
    AssetDTO findAssetById(int id);
    void updateAsset(AssetDTO asset, MultipartFile file, MultipartFile image, List<MultipartFile> gallery) throws IOException;
    void deleteAsset(int id);
    List<AssetDTO> findAllAssets();
    List<AssetDTO> findAllAssetsByUserIdAndAssetType(int id, String assetType, String sortBy, String sortType);
    List<AssetDTO> findAllAssets(AssetSpecification assetSpecification);
    void increaseDownloadsCount(int id);
    void manageLikes(int assetId, int userId);
    List<AssetDTO> findFavoritesByUserId(int userId);
    FilterDataDTO findAllAssetTagsAndExtensions(AssetType assetType);

}
