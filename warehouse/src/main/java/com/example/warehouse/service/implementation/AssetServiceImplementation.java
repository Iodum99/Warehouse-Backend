package com.example.warehouse.service.implementation;

import com.example.warehouse.dto.AssetDTO;
import com.example.warehouse.dto.AssetListViewDTO;
import com.example.warehouse.dto.NewAssetDTO;
import com.example.warehouse.dto.UserDTO;
import com.example.warehouse.exception.AssetNotFoundException;
import com.example.warehouse.exception.UserNotFoundException;
import com.example.warehouse.model.Asset;
import com.example.warehouse.model.User;
import com.example.warehouse.repository.AssetRepository;
import com.example.warehouse.repository.UserRepository;
import com.example.warehouse.service.AssetService;
import com.example.warehouse.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
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
    private final UserRepository userRepository;
    private static final String directoryPath = System.getProperty("user.dir") + "/assets/user_id_";
    ModelMapper modelMapper = new ModelMapper();
    @Override
    public void createAsset(NewAssetDTO newAssetDTO, MultipartFile file, MultipartFile image, List<MultipartFile> gallery) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Asset asset = modelMapper.map(newAssetDTO, Asset.class);
        User author = userRepository.findById(newAssetDTO.getUserId())
                .orElseThrow(() -> new UserNotFoundException("UserId: " + newAssetDTO.getUserId()));
        asset.setAuthor(author.getUsername());
        asset.setUploadDate(LocalDate.now());
        String path = directoryPath + author.getId() + "/" + author.getUsername() + "_" + newAssetDTO.getName().replace(" ","_") + ".zip";
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
        return modelMapper.map(asset, AssetDTO.class);
    }

    @Override
    public void updateAsset(AssetDTO assetDTO, MultipartFile file, MultipartFile image, List<MultipartFile> gallery) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Asset asset = assetRepository.findById(assetDTO.getId())
                .orElseThrow(() -> new AssetNotFoundException("AssetId: " + assetDTO.getId()));
        asset.setName(assetDTO.getName());
        asset.setDescription(asset.getDescription());
        asset.setLastModifiedDate(LocalDate.now());
        try{
            if(image != null)
                asset.setImage(image.getBytes());
            if(file != null){
                renameFileOnAssetUpdate(asset, assetDTO.getName(), file);
            }
            if(gallery != null)
                asset.setGallery(getByteArrayForGallery(gallery));
            assetRepository.save(asset);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void renameFileOnAssetUpdate(Asset asset, String name, MultipartFile file) throws IOException {
        User author = userRepository.findById(asset.getUserId())
                .orElseThrow(() -> new UserNotFoundException("UserId: " + asset.getUserId()));
        File originalFile = new File(asset.getFilePath());
        String path = directoryPath + author.getId() + "/" + author.getUsername() + "_" + name.replace(" ","_") + ".zip";
        File newFile = new File(path);
        if(originalFile.renameTo(newFile))
            Files.write(Path.of(path), file.getBytes());
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
    public List<AssetListViewDTO> findAllAssetsByUserId(int userId) {
        return modelMapper
                .map(assetRepository.findAllByUserId(userId), new TypeToken<List<AssetListViewDTO>>(){}.getType());
    }
}
