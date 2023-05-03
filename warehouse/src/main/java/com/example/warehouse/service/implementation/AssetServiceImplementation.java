package com.example.warehouse.service.implementation;

import com.example.warehouse.dto.AssetDTO;
import com.example.warehouse.dto.NewAssetDTO;
import com.example.warehouse.exception.AssetNotFoundException;
import com.example.warehouse.exception.UserNotFoundException;
import com.example.warehouse.model.Asset;
import com.example.warehouse.model.AssetType;
import com.example.warehouse.model.User;
import com.example.warehouse.repository.AssetRepository;
import com.example.warehouse.repository.UserRepository;
import com.example.warehouse.service.AssetService;
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
    private static final String DIRECTORY = "..\\..\\Warehouse-Frontend\\warehouse\\src\\assets\\user_id_%d\\asset_id_%d";
    ModelMapper modelMapper = new ModelMapper();
    @Override
    public AssetDTO createAsset(NewAssetDTO newAssetDTO, MultipartFile file, MultipartFile image, List<MultipartFile> gallery) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Asset asset = assetRepository.save(modelMapper.map(newAssetDTO, Asset.class));
        User author = userRepository.findById(newAssetDTO.getUserId())
                .orElseThrow(() -> new UserNotFoundException("UserId: " + newAssetDTO.getUserId()));
        String assetDirectory = DIRECTORY.formatted(author.getId(), asset.getId());
        try{
            createAssetDirectory(newAssetDTO.getUserId(), asset.getId());
            String fileName = newAssetDTO.getName().replaceAll("[^a-zA-Z0-9.-]","");
            String assetPath = assetDirectory + "/%s_%s.zip".formatted(author.getUsername(), fileName);
            Files.write(Path.of(assetPath), file.getBytes());
            asset.setFilePath(assetPath);
            asset.setImagePaths(getImagePaths(image, gallery, assetDirectory));
            asset.setAuthor(author.getUsername());

        } catch (Exception e){
            e.printStackTrace();
        }
        return modelMapper.map(assetRepository.save(asset), AssetDTO.class);
    }

    private List<String> getImagePaths(MultipartFile image, List<MultipartFile> gallery, String directory){
        List<String> paths = new ArrayList<>();
        try{
            //Check if it's null for Editing to skip writing
            if(image != null)
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
            Files.createDirectories(Paths.get("..\\..\\Warehouse-Frontend\\warehouse\\src\\assets\\user_id_" + userId
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
        asset = modelMapper.map(assetDTO, Asset.class);
        asset.setLastModifiedDate(LocalDate.now());
        String assetDirectory = DIRECTORY.formatted(asset.getUserId(), asset.getId());
        try{
            if(file != null){
                String newFileName =  renameFileOnAssetUpdate(asset, assetDTO.getName(), file);
                asset.setFilePath(newFileName);
            }
            if(image != null){
                Files.write(Path.of(asset.getImagePaths().get(0)), image.getBytes());
            }

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

        List<String> newImagePaths;
        if(gallery != null) {
            if (originalPaths.size() > 1)
                for (int i = 1; i < originalPaths.size(); i++)
                    Files.deleteIfExists(Path.of(originalPaths.get(i)));
            newImagePaths = getImagePaths(image, gallery, assetDirectory);
        } else {
            newImagePaths = originalPaths;
        }
        //TODO: Else pass gallery as null to remove entire gallery

        return newImagePaths;
    }

    private String renameFileOnAssetUpdate(Asset asset, String name, MultipartFile file) throws IOException {
        User author = userRepository.findById(asset.getUserId())
                .orElseThrow(() -> new UserNotFoundException("UserId: " + asset.getUserId()));
        File originalFile = new File(asset.getFilePath());
        String assetDirectory = DIRECTORY.formatted(author.getId(), asset.getId());
        String fileName = name.replaceAll("[^a-zA-Z0-9.-]","");
        String assetPath = assetDirectory + "\\%s_%s.zip".formatted(author.getUsername(), fileName);
        File newFile = new File(assetPath);
        if(originalFile.renameTo(newFile))
            Files.write(Path.of(assetPath), file.getBytes());
        return assetPath;
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
    public List<AssetDTO> findAllAssets() {
        return modelMapper
                .map(assetRepository.findAll(), new TypeToken<List<AssetDTO>>(){}.getType());
    }

    @Override
    public List<AssetDTO> findAllAssetsByUserIdAndAssetType(int userId, String type) {
        return modelMapper
                .map(assetRepository.findAllByUserIdAndAssetType(userId, AssetType.valueOf(type.toUpperCase())),
                        new TypeToken<List<AssetDTO>>(){}.getType());
    }

    @Override
    public List<AssetDTO> findAllAssetsByType(String type) {
       return modelMapper
                .map(assetRepository.findAllByAssetType(AssetType.valueOf(type.toUpperCase())),
                        new TypeToken<List<AssetDTO>>(){}.getType());
    }

    @Override
    public void increaseDownloadsCount(int id) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new AssetNotFoundException("AssetId: " + id));
        int downloadsCount = asset.getDownloads();
        asset.setDownloads(++downloadsCount);
        assetRepository.save(asset);
    }

    @Override
    public void manageLikes(int assetId, int userId) {
        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new AssetNotFoundException("AssetId: " + assetId));
        if(asset.getUserIdLikes().contains(userId))
            asset.getUserIdLikes().remove((Integer) userId);
        else
            asset.getUserIdLikes().add(userId);
        assetRepository.save(asset);
    }

    @Override
    public List<AssetDTO> findFavoritesByUserId(int userId) {
        return modelMapper
                .map(assetRepository.findFavoritesByUserId(userId),
                        new TypeToken<List<AssetDTO>>(){}.getType());
    }
}
