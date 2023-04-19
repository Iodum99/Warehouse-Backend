package com.example.warehouse.service.implementation;

import com.example.warehouse.dto.AssetDTO;
import com.example.warehouse.dto.NewAssetDTO;
import com.example.warehouse.model.Asset;
import com.example.warehouse.repository.AssetRepository;
import com.example.warehouse.repository.UserRepository;
import com.example.warehouse.service.AssetService;
import com.example.warehouse.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDate;


@Service
@RequiredArgsConstructor
public class AssetServiceImplementation implements AssetService {

    private final AssetRepository assetRepository;
    private final UserService userService;
    ModelMapper modelMapper = new ModelMapper();
    @Override
    public void createAsset(NewAssetDTO newAssetDTO, MultipartFile file, MultipartFile image) {
        Asset asset = modelMapper.map(newAssetDTO, Asset.class);
        asset.setUploadDate(LocalDate.now());

        try{
            asset.setImage(image.getBytes());
            assetRepository.save(asset);
            //TODO: Save File and store its path
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public AssetDTO findAssetById(int id) {
        AssetDTO asset = modelMapper.map(assetRepository.findById(id).orElseThrow(), AssetDTO.class);
        asset.setAuthor(userService.findUserById(id).getUsername());
        return asset;
    }
}
