package com.example.warehouse.repository;

import com.example.warehouse.model.Asset;
import com.example.warehouse.model.AssetType;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Integer> {

    List<Asset> findAllByUserIdAndAssetType(int userId, AssetType assetType, Sort sort);
    List<Asset> findAllByAssetType(AssetType assetType, Sort sort);

    @Query("select a from Asset a where :userId IN elements(a.userIdLikes)")
    List<Asset> findFavoritesByUserId(int userId);
}
