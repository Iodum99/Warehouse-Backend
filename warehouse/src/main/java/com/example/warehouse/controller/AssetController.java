package com.example.warehouse.controller;

import com.example.warehouse.dto.AssetDTO;
import com.example.warehouse.dto.NewAssetDTO;
import com.example.warehouse.service.AssetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(value = "/api/asset")
@CrossOrigin("http://localhost:4200")
@RequiredArgsConstructor
public class AssetController {

    private final AssetService assetService;
    @PostMapping
    public ResponseEntity<?> createAsset(
            @RequestPart("asset") NewAssetDTO newAssetDTO,
            @RequestPart("file") MultipartFile file,
            @RequestPart("image") MultipartFile image,
            @RequestPart(value = "gallery", required = false) List<MultipartFile> gallery){
        AssetDTO assetDTO = assetService.createAsset(newAssetDTO, file, image, gallery);
        return new ResponseEntity<>(assetDTO, HttpStatus.OK);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getAssetById(@PathVariable int id){
        return new ResponseEntity<>(assetService.findAssetById(id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAssetById(@PathVariable int id){
        assetService.deleteAsset(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<?> updateAsset(
            @RequestPart("asset") AssetDTO assetDTO,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @RequestPart(value = "gallery", required = false) List<MultipartFile> gallery){
        assetService.updateAsset(assetDTO, file, image, gallery);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/user/{id}/type/{type}")
    public ResponseEntity<?> getAllAssetsByUserIdAndAssetType(
            @PathVariable int id,
            @PathVariable String type){
        return new ResponseEntity<>(assetService.findAllAssetsByUserIdAndAssetType(id, type), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> getAllAssets(){
        return new ResponseEntity<>(assetService.findAllAssets(), HttpStatus.OK);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<?> getAllAssetsByType(@PathVariable String type){
        return new ResponseEntity<>(assetService.findAllAssetsByType(type), HttpStatus.OK);
    }

    @PutMapping("/downloads/{id}")
    public ResponseEntity<?> increaseDownloadsCount(@PathVariable int id){
        assetService.increaseDownloadsCount(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/likes/{assetId}_{userId}")
    public ResponseEntity<?> manageLikes(
            @PathVariable int assetId,
            @PathVariable int userId){
        assetService.manageLikes(assetId, userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/favorites/{id}")
    public ResponseEntity<?> getFavoritesByUserId(@PathVariable int id){
        return new ResponseEntity<>(assetService.findFavoritesByUserId(id), HttpStatus.OK);
    }
}
