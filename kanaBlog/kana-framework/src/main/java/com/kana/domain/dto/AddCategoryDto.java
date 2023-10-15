package com.kana.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddCategoryDto {
    private String name;
    private String description;
    private String status;
    private Long id;
}
