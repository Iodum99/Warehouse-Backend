package com.example.warehouse.service.implementation;

import com.example.warehouse.dto.AssetDTO;
import com.example.warehouse.dto.AssetListViewDTO;
import com.example.warehouse.dto.NewAssetDTO;
import com.example.warehouse.dto.UserDTO;
import com.example.warehouse.exception.AssetNotFoundException;
import com.example.warehouse.model.Asset;
import com.example.warehouse.repository.AssetRepository;
import com.example.warehouse.service.AssetService;
import com.example.warehouse.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class AssetServiceImplementation implements AssetService {

    private final AssetRepository assetRepository;
    private final UserService userService;
    private static final String directoryPath = System.getProperty("user.dir") + "/assets/user_id_";
    ModelMapper modelMapper = new ModelMapper();
    @Override
    public void createAsset(NewAssetDTO newAssetDTO, MultipartFile file, MultipartFile image, List<MultipartFile> gallery) {
        Asset asset = modelMapper.map(newAssetDTO, Asset.class);
        asset.setUploadDate(LocalDate.now());
        String author = userService.findUserById(newAssetDTO.getUserId()).getUsername();
        String path = directoryPath + newAssetDTO.getUserId() + "/" + author + "_" + newAssetDTO.getName() + ".zip";
        try{
            if(gallery != null)
                asset.setGallery(getByteArrayForGallery(gallery));
            Files.write(Path.of(path), file.getBytes());
            asset.setFilePath(path);
            asset.setImage(image.getBytes());
            assetRepository.save(asset);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private List<byte[]> getByteArrayForGallery(List<MultipartFile> gallery) throws IOException {
        List<byte[]> galleryBytes = new ArrayList<>();
        for(MultipartFile image: gallery)
            galleryBytes.add(image.getBytes());

        return galleryBytes;
    }

    @Override
    public AssetDTO findAssetById(int id) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new AssetNotFoundException("AssetId: " + id));
        AssetDTO assetDTO = modelMapper.map(asset, AssetDTO.class);
        assetDTO.setAuthor(userService.findUserById(id).getUsername());
        return assetDTO;
    }

    @Override
    public void updateAsset(AssetDTO assetDTO, MultipartFile file, MultipartFile image, List<MultipartFile> gallery) {
        Asset asset = assetRepository.findById(assetDTO.getId())
                .orElseThrow(() -> new AssetNotFoundException("AssetId: " + assetDTO.getId()));
        asset.setName(assetDTO.getName());
        asset.setDescription(asset.getDescription());
        asset.setLastModifiedDate(LocalDate.now());
        try{
            if(image != null)
                asset.setImage(image.getBytes());
            if(file != null)
                Files.write(Path.of(assetDTO.getFilePath()), file.getBytes());
            if(gallery != null)
                asset.setGallery(getByteArrayForGallery(gallery));
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

    @Override
    public List<AssetListViewDTO> findAllAssets() {
        return modelMapper
                .map(assetRepository.findAll(), new TypeToken<List<AssetListViewDTO>>(){}.getType());
    }

    @Override
    public List<AssetListViewDTO> findAllAssetsByUserId(int id) {
git s        return modelMapper
                .map(assetRepository.findAllByUserId(id), new TypeToken<List<AssetListViewDTO>>(){}.getType());
    }
}
