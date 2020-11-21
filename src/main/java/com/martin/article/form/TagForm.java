package com.martin.article.form;

import com.martin.article.model.Tag;
import lombok.Data;

@Data
public class TagForm {
    private String name;

    public Tag toTag(){
        Tag tag = new Tag();
        tag.setName(name);

        return tag;
    }
}
