package com.example.warehouse.controller;

import com.example.warehouse.dto.AssetDTO;
import com.example.warehouse.dto.NewAssetDTO;
import com.example.warehouse.dto.NewUserDTO;
import com.example.warehouse.service.AssetService;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
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
            @RequestPart("gallery") List<MultipartFile> gallery){
        assetService.createAsset(newAssetDTO, file, image, gallery);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @GetMapping("/item/{id}")
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

    @GetMapping("/user/{id}")
    public ResponseEntity<?> getAllAssetsByUserId(@PathVariable int id){
        return new ResponseEntity<>(assetService.findAllAssetsByUserId(id), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> getAllAssets(){
        return new ResponseEntity<>(assetService.findAllAssets(), HttpStatus.OK);
    }

    @GetMapping("/{type}")
    public ResponseEntity<?> getAllAssetsByType(@PathVariable String type){
        return new ResponseEntity<>(assetService.findAllAssetsByType(type), HttpStatus.OK);
    }
}
