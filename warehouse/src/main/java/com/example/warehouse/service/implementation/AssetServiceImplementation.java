package com.example.warehouse.service.implementation;

import com.example.warehouse.dto.AssetDTO;
import com.example.warehouse.dto.NewAssetDTO;
import com.example.warehouse.exception.AssetNotFoundException;
import com.example.warehouse.exception.AssetUnsupportedExtensionException;
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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


@Service
@RequiredArgsConstructor
public class AssetServiceImplementation implements AssetService {

    private final AssetRepository assetRepository;
    private final UserRepository userRepository;
    private static final Map<AssetType, List<String>> SUPPORTED_EXTENSIONS = createMap();
    private static Map<AssetType, List<String>> createMap() {
        return Map.of(
                AssetType.OBJECT, new ArrayList<>(Arrays.asList("3ds", "fbx", "obj", "dae")),
                AssetType.TEXTURE, new ArrayList<>(Arrays.asList("png", "bmp", "tga", "jpg")),
                AssetType.AUDIO, new ArrayList<>(Arrays.asList("mp3", "wav", "flac")),
                AssetType.ANIMATION, new ArrayList<>(Arrays.asList("fbx", "3ds")));
    }
    private static final String DIRECTORY = "..\\..\\Warehouse-Frontend\\warehouse\\src\\assets\\user_id_%d\\asset_id_%d";
    ModelMapper modelMapper = new ModelMapper();
    @Override
    public AssetDTO createAsset(
            NewAssetDTO newAssetDTO,
            MultipartFile file,
            MultipartFile image,
            List<MultipartFile> gallery) throws IOException {

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        List<String> extensions = getExtensions(newAssetDTO.getAssetType(), file);
        Asset asset = assetRepository.save(modelMapper.map(newAssetDTO, Asset.class));
        User author = userRepository.findById(newAssetDTO.getUserId())
                .orElseThrow(() -> new UserNotFoundException("UserId: " + newAssetDTO.getUserId()));
        String assetDirectory = DIRECTORY.formatted(author.getId(), asset.getId());
            asset.setExtensions(extensions);
            String fileName = newAssetDTO.getName().replaceAll("[^a-zA-Z0-9.-]","");
            String assetPath = assetDirectory + "/%s_%s.zip".formatted(author.getUsername(), fileName);
            createAssetDirectory(newAssetDTO.getUserId(), asset.getId());
            Files.write(Path.of(assetPath), file.getBytes());
            asset.setSize(file.getSize());
            asset.setFilePath(assetPath);
            asset.setImagePaths(getImagePaths(image, gallery, assetDirectory));
            asset.setAuthor(author.getUsername());
        return modelMapper.map(assetRepository.save(asset), AssetDTO.class);
    }
    private List<String> getExtensions(AssetType type, MultipartFile file) throws IOException {

        Path path = Path.of("..\\..\\Warehouse-Frontend\\warehouse\\src\\assets\\temp\\temp.zip");
        Files.write(path, file.getBytes());
        List<String> extensions = readExtensionsFromZippedFile();
        List<String> validExtensions = new ArrayList<>();
            //Check if found extension matches supported for required asset type
            List<String> supportedExtensions = SUPPORTED_EXTENSIONS.get(type);
            for(String extension: extensions){
                if(supportedExtensions.contains(extension))
                    validExtensions.add(extension);
            }
            if(validExtensions.size() == 0){
                Files.deleteIfExists(path);
                throw new AssetUnsupportedExtensionException(type.toString());
            }
        Files.deleteIfExists(path);
        return validExtensions;
    }

    private List<String> readExtensionsFromZippedFile() throws IOException {

        List<String> extensions = new ArrayList<>();
        Set<String> set = new HashSet<>(extensions);
        File asset = new File("..\\..\\Warehouse-Frontend\\warehouse\\src\\assets\\temp\\temp.zip");
        InputStream in = new FileInputStream(asset);
        ZipInputStream zis = new ZipInputStream(in);
        ZipEntry entry;
        byte[] buffer = new byte[1024];
        //Iterate through archive and extract all extensions
        while ((entry = zis.getNextEntry())!= null) {
            while ((zis.read(buffer, 0, 1024)) >= 0) {
                set.add(entry.getName().split("\\.")[1]);
            }
        }
        extensions.addAll(set);
        zis.close();
        return extensions;
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
    public void updateAsset(AssetDTO assetDTO, MultipartFile file, MultipartFile image, List<MultipartFile> gallery) throws IOException {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Asset asset = modelMapper.map(assetDTO, Asset.class);
        asset.setLastModifiedDate(LocalDate.now());
        String assetDirectory = DIRECTORY.formatted(asset.getUserId(), asset.getId());
        if(file != null){
            String fileName = assetDTO.getName().replaceAll("[^a-zA-Z0-9.-]","");
            String newAssetPath = assetDirectory + "\\%s_%s.zip".formatted(assetDTO.getAuthor(), fileName);
            asset.setExtensions(getExtensions(asset.getAssetType(), file));
            asset.setSize(file.getSize());
            renameFileOnAssetUpdate(asset, newAssetPath, file);
            asset.setFilePath(newAssetPath);
        }
        if(image != null){
            Files.write(Path.of(asset.getImagePaths().get(0)), image.getBytes());
        }
        asset.setImagePaths(updateImages(asset.getImagePaths(), gallery, image, assetDirectory));
        assetRepository.save(asset);
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

    private void renameFileOnAssetUpdate(Asset asset, String newPath, MultipartFile file) throws IOException {
        File originalFile = new File(asset.getFilePath());
        File newFile = new File(newPath);
        if(originalFile.renameTo(newFile))
            Files.write(Path.of(newPath), file.getBytes());
    }

    @Override
    public void deleteAsset(int id) {
        Asset asset = assetRepository.findById(id).orElseThrow(() -> new AssetNotFoundException("AssetId: " + id));
       /*
        try {
            Files.deleteIfExists(Path.of(asset.getFilePath()));
            for(String path: asset.getImagePaths())
                Files.deleteIfExists(Path.of(path));
            Files.deleteIfExists(Path.of(DIRECTORY.formatted(asset.getUserId(), asset.getId())));
            assetRepository.deleteById(id);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        */
    }

    @Override
    public List<AssetDTO> findAllAssets() {
        return modelMapper
                .map(assetRepository.findAll(Sort.by("upload_date")), new TypeToken<List<AssetDTO>>(){}.getType());
    }

    @Override
    public List<AssetDTO> findAllAssetsByUserIdAndAssetType(int userId, String type, String sortBy, String sortType ) {
        return modelMapper
                .map(assetRepository.findAllByUserIdAndAssetType(userId, AssetType.valueOf(type.toUpperCase()),
                            Sort.by(Sort.Direction.valueOf(sortType),sortBy)),
                        new TypeToken<List<AssetDTO>>(){}.getType());
    }

    @Override
    public List<AssetDTO> findAllAssetsByType(String type, String sortBy, String sortType) {
       return modelMapper
                .map(assetRepository.findAllByAssetType(AssetType.valueOf(type.toUpperCase()),
                                Sort.by(Sort.Direction.valueOf(sortType),sortBy)),
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
        asset.setNumberOfLikes(asset.getUserIdLikes().size());
        assetRepository.save(asset);
    }

    @Override
    public List<AssetDTO> findFavoritesByUserId(int userId) {
        return modelMapper
                .map(assetRepository.findFavoritesByUserId(userId),
                        new TypeToken<List<AssetDTO>>(){}.getType());
    }
}
