package com.example.warehouse.service.implementation;

import com.example.warehouse.dto.AssetDTO;
import com.example.warehouse.dto.AssetListViewDTO;
import com.example.warehouse.dto.NewAssetDTO;
import com.example.warehouse.dto.UserDTO;
import com.example.warehouse.exception.AssetNotFoundException;
import com.example.warehouse.exception.UserNotFoundException;
import com.example.warehouse.model.Asset;
import com.example.warehouse.model.AssetType;
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
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class AssetServiceImplementation implements AssetService {

    private final AssetRepository assetRepository;
    private final UserRepository userRepository;
    private static final String DIRECTORY = System.getProperty("user.dir") + "\\assets\\user_id_%d\\asset_id_%d";
    ModelMapper modelMapper = new ModelMapper();
    @Override
    public void createAsset(NewAssetDTO newAssetDTO, MultipartFile file, MultipartFile image, List<MultipartFile> gallery) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Asset asset = assetRepository.save(modelMapper.map(newAssetDTO, Asset.class));
        User author = userRepository.findById(newAssetDTO.getUserId())
                .orElseThrow(() -> new UserNotFoundException("UserId: " + newAssetDTO.getUserId()));
        String assetDirectory = DIRECTORY.formatted(author.getId(), asset.getId());
        try{
            createAssetDirectory(newAssetDTO.getUserId(), asset.getId());
            String assetPath = assetDirectory + "/%s_%s.zip".formatted(author.getUsername(), newAssetDTO.getName().replace(" ", "_"));
            Files.write(Path.of(assetPath), file.getBytes());
            asset.setFilePath(assetPath);
            asset.setImagePaths(getImagePaths(image, gallery, assetDirectory));
            asset.setAuthor(author.getUsername());
            asset.setUploadDate(LocalDate.now());
            assetRepository.save(asset);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private List<String> getImagePaths(MultipartFile image, List<MultipartFile> gallery, String directory){
        List<String> paths = new ArrayList<>();
        try{
            Files.write(Path.of(directory + "\\main.jpg"), image.getBytes());
            paths.add(directory + "\\main.jpg");
            int index = 1;
            if(gallery != null){
                for(MultipartFile galleryImage: gallery){
                    Files.write(Path.of(directory + "\\gallery" + index + ".jpg"), galleryImage.getBytes());
                    paths.add(directory + "\\gallery" + index + ".jpg");
                    index++;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return paths;
    }

    private void createAssetDirectory(int userId, int assetId) {
        try {
            Files.createDirectories(Paths.get(System.getProperty("user.dir") +
                    "\\assets\\user_id_" + userId
                    + "\\asset_id_" + assetId));
        } catch (Exception e){
            e.printStackTrace();
        }
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
        String assetDirectory = DIRECTORY.formatted(asset.getUserId(), asset.getId());
        try{
           //TODO: Edit image and gallery
            if(file != null)
                renameFileOnAssetUpdate(asset, assetDTO.getName(), file);
            Files.write(Path.of(asset.getImagePaths().get(0)), image.getBytes());
            asset.setImagePaths(updateImages(asset.getImagePaths(), gallery, image, assetDirectory));
            assetRepository.save(asset);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private List<String> updateImages(
            List<String> originalPaths,
            List<MultipartFile> gallery,
            MultipartFile image,
            String assetDirectory) throws IOException {

        List<String> newImagePaths = new ArrayList<>();

        if(gallery != null){
            if(originalPaths.size() > 1){
                for(int i = 1; i < originalPaths.size(); i++)
                    Files.deleteIfExists(Path.of(originalPaths.get(i)));
            }
            newImagePaths = getImagePaths(image, gallery, assetDirectory );
        } else if (originalPaths.size() > 1){
            for(int i = 1; i < originalPaths.size(); i++)
                Files.deleteIfExists(Path.of(originalPaths.get(i)));
            newImagePaths = getImagePaths(image, null, assetDirectory );
        }

        return newImagePaths;
    }

    private void renameFileOnAssetUpdate(Asset asset, String name, MultipartFile file) throws IOException {
        User author = userRepository.findById(asset.getUserId())
                .orElseThrow(() -> new UserNotFoundException("UserId: " + asset.getUserId()));
        File originalFile = new File(asset.getFilePath());
        String assetDirectory = DIRECTORY.formatted(author.getId(), asset.getId());
        String assetPath = assetDirectory + "/%s_%s.zip".formatted(author.getUsername(), name.replace(" ", "_"));
        File newFile = new File(assetPath);
        if(originalFile.renameTo(newFile))
            Files.write(Path.of(assetPath), file.getBytes());
    }

    @Override
    public void deleteAsset(int id) {
        Asset asset = assetRepository.findById(id).orElseThrow(() -> new AssetNotFoundException("AssetId: " + id));
        try {
            Files.deleteIfExists(Path.of(asset.getFilePath()));
            for(String path: asset.getImagePaths())
                Files.deleteIfExists(Path.of(path));
            Files.deleteIfExists(Path.of(DIRECTORY.formatted(asset.getUserId(), asset.getId())));
            assetRepository.deleteById(id);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    @Override
    public List<AssetListViewDTO> findAllAssetsByType(String type) {
       return modelMapper
                .map(assetRepository.findAllByAssetType(AssetType.valueOf(type)),
                        new TypeToken<List<AssetListViewDTO>>(){}.getType());
    }
}
