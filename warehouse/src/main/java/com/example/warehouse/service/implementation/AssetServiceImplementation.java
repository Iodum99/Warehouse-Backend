package com.example.warehouse.service.implementation;

import com.example.warehouse.dto.AssetDTO;
import com.example.warehouse.dto.NewAssetDTO;
import com.example.warehouse.exception.AssetNotFoundException;
import com.example.warehouse.exception.UserNotFoundException;
import com.example.warehouse.model.Asset;
import com.example.warehouse.repository.AssetRepository;
import com.example.warehouse.repository.UserRepository;
import com.example.warehouse.service.AssetService;
import com.example.warehouse.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;


@Service
@RequiredArgsConstructor
public class AssetServiceImplementation implements AssetService {

    private final AssetRepository assetRepository;
    private final UserService userService;
    private static final String directoryPath = System.getProperty("user.dir") + "/assets/user_id_";
    ModelMapper modelMapper = new ModelMapper();
    @Override
    public void createAsset(NewAssetDTO newAssetDTO, MultipartFile file, MultipartFile image) {
        Asset asset = modelMapper.map(newAssetDTO, Asset.class);
        asset.setUploadDate(LocalDate.now());
        String author = userService.findUserById(newAssetDTO.getUserId()).getUsername();
        String path = directoryPath + newAssetDTO.getUserId() + "/" + author + "_" + newAssetDTO.getName() + ".zip";
        try{
            Files.write(Path.of(path), file.getBytes());
            asset.setFilePath(path);
            asset.setImage(image.getBytes());
            assetRepository.save(asset);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public AssetDTO findAssetById(int id) {
        AssetDTO asset = modelMapper.map(assetRepository.findById(id)
                .orElseThrow(() -> new AssetNotFoundException("AssetId: " + id)), AssetDTO.class);
        asset.setAuthor(userService.findUserById(id).getUsername());
        return asset;
    }

    @Override
    public void updateAsset(AssetDTO assetDTO, MultipartFile file, MultipartFile image) {
        Asset asset = modelMapper.map(assetDTO, Asset.class);
        try{
            if(image != null)
                asset.setImage(image.getBytes());
            if(file != null){
                Files.write(Path.of(assetDTO.getFilePath()), file.getBytes());
            }
            asset.setLastModifiedDate(LocalDate.now());
            assetRepository.save(asset);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void deleteAsset(int id) {
        Asset asset = assetRepository.findById(id).orElseThrow(() -> new AssetNotFoundException("AssetId: " + id));
        try {
            Files.deleteIfExists(Path.of(asset.getFilePath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assetRepository.deleteById(id);
    }
}
