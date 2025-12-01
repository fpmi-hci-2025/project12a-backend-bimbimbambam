package com.example.techstore.catalog.dto;

import lombok.Data;

@Data
public class CategoryDTO {
    private Integer id;
    private String name;
    private Integer parentId;
}