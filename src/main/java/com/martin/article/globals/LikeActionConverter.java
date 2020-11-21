package com.martin.article.globals;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class LikeActionConverter implements Converter<String, LikeActionEnum> {
    @Override
    public LikeActionEnum convert(String s) {
        return LikeActionEnum.valueOf(s.toUpperCase());
    }
}
