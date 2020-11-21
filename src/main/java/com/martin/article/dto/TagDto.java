package com.martin.article.dto;

import com.martin.article.model.Tag;
import lombok.Data;

@Data
public class TagDto {

    private Long id;
    private String name;

    public TagDto(Tag tag){
        id = tag.getId();
        name = tag.getName();
    }
}
