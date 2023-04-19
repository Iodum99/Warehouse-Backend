package com.example.warehouse.controller;

import com.example.warehouse.dto.AssetDTO;
import com.example.warehouse.dto.NewAssetDTO;
import com.example.warehouse.service.AssetService;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
            @RequestPart("image") MultipartFile image){
        assetService.createAsset(newAssetDTO, file, image);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getAssetById(@PathVariable int id){
        return new ResponseEntity<>(assetService.findAssetById(id), HttpStatus.OK);
    }
}
