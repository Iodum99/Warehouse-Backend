package com.example.warehouse.model.helper;

import com.example.warehouse.model.AssetType;
import lombok.Data;
import org.springframework.data.domain.Sort;

import java.util.List;

@Data
public class AssetSearchRequest {

    private int userId;
    private AssetType type;
    private String sortBy;
    private Sort.Direction sortDirection;
    private String filterText;
    private List<String> filterTags;
    private List<String> filterExtensions;

    public AssetSearchRequest(int userId,
                              String type,
                              String sortBy,
                              String direction,
                              String text,
                              List<String> extensions,
                              List<String> tags){

        this.userId = userId;
        this.type = AssetType.valueOf(type.toUpperCase());
        this.sortBy = sortBy;
        this.sortDirection = Sort.Direction.valueOf(direction);
        this.filterText = text;
        this.filterTags = tags;
        this.filterExtensions = extensions;

    }


}
