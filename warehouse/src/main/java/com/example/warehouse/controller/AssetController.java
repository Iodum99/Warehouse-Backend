package com.example.warehouse.controller;

import com.example.warehouse.dto.AssetDTO;
import com.example.warehouse.dto.NewAssetDTO;
import com.example.warehouse.model.AssetType;
import com.example.warehouse.model.helper.AssetSearchRequest;
import com.example.warehouse.service.AssetService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
            @RequestPart(value = "gallery", required = false) List<MultipartFile> gallery) throws IOException {
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
            @RequestPart(value = "gallery", required = false) List<MultipartFile> gallery) throws IOException {
        assetService.updateAsset(assetDTO, file, image, gallery);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<?> getAllAssets(
            @RequestParam(value = "userId", required = false) int id,
            @RequestParam("type") String type,
            @RequestParam("sortBy") String sortBy,
            @RequestParam("sortDirection") String sortDirection,
            @RequestParam(value = "filterByText", required = false) String text,
            @RequestParam(value = "filterByExtensions", required = false) List<String> extensions,
            @RequestParam(value = "filterByTags", required = false) List<String> tags
            ){
        return new ResponseEntity<>(assetService.findAllAssets(
                new AssetSearchRequest(id, type, sortBy, sortDirection, text, extensions, tags)), HttpStatus.OK);
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

    @GetMapping("/filterdata/{type}")
    public ResponseEntity<?> getTagsAndExtensions(@PathVariable String type){
        return new ResponseEntity<>(assetService.findAllAssetTagsAndExtensions
                (AssetType.valueOf(type.toUpperCase())), HttpStatus.OK);
    }

    @GetMapping("/filterdata/{type}/user/{id}")
    public ResponseEntity<?> getTagsAndExtensions(
            @PathVariable String type,
            @PathVariable int id){
        return new ResponseEntity<>(assetService.findAllAssetTagsAndExtensionsByUser
                (AssetType.valueOf(type.toUpperCase()), id), HttpStatus.OK);
    }

}
