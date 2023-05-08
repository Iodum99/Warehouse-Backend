package com.example.warehouse.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class FilterDataDTO {

    private List<String> tags;
    private List<String> extensions;
}
