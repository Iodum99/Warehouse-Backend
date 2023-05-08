package com.example.warehouse.repository;

import com.example.warehouse.model.Asset;
import com.example.warehouse.model.AssetType;
import com.example.warehouse.model.helper.AssetSearchRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Integer>, JpaSpecificationExecutor<Asset> {

    @Query("select a from Asset a where :userId IN elements(a.userIdLikes)")
    List<Asset> findFavoritesByUserId(int userId);

    @Query("select DISTINCT a.tags from Asset a where a.assetType = :assetType")
    List<String> findTags(AssetType assetType);

    @Query("select DISTINCT a.extensions from Asset a where a.assetType = :assetType")
    List<String> findExtensions(AssetType assetType);

    @Query("select DISTINCT a.extensions from Asset a where a.assetType = :assetType" +
            " AND :id = a.userId")
    List<String> findExtensionsByUser(AssetType assetType, int id);

    @Query("select DISTINCT a.tags from Asset a where a.assetType = :assetType" +
            " AND :id = a.userId")
    List<String> findTagsByUser(AssetType assetType, int id);
}
