package com.example.warehouse.model.helper;

import com.example.warehouse.model.AssetType;
import lombok.Data;
import org.springframework.data.domain.Sort;

import java.util.List;

@Data
public class AssetSpecification {

    private AssetType type;
    private String sortBy;
    private Sort.Direction sortDirection;
    private String filterText;
    private List<String> filterTags;
    private List<String> filterExtensions;

    public AssetSpecification(String type,
                              String sortBy,
                              String direction,
                              String text,
                              List<String> tags,
                              List<String> extensions){

        this.type = AssetType.valueOf(type.toUpperCase());
        this.sortBy = sortBy;
        this.sortDirection = Sort.Direction.valueOf(direction);
        this.filterText = text;
        this.filterTags = tags;
        this.filterExtensions = extensions;

    }


}
