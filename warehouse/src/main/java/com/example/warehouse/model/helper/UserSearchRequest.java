package com.example.warehouse.model.helper;

import lombok.Data;
import org.springframework.data.domain.Sort;

@Data
public class UserSearchRequest {

    private String sortBy;
    private Sort.Direction sortDirection;
    private String filterText;

    public UserSearchRequest(String sortBy,
                             String sortDirection,
                             String text
                             ){
        this.sortBy = sortBy;
        this.sortDirection = Sort.Direction.valueOf(sortDirection);
        this.filterText = text;
    }


}
