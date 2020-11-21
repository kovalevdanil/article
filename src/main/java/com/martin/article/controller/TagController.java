package com.martin.article.controller;

import com.github.slugify.Slugify;
import com.martin.article.dto.TagDto;
import com.martin.article.exception.tag.TagNotFoundException;
import com.martin.article.form.TagForm;
import com.martin.article.model.Tag;
import com.martin.article.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/tags", produces = APPLICATION_JSON_VALUE)
public class TagController {

    private final TagRepository tagRepository;

    @Autowired
    public TagController(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @GetMapping
    public ResponseEntity<?> getTags(@RequestParam(name = "page", required = false) Integer page,
                                     @RequestParam(name = "size", required = false) Integer size){
        if (Objects.isNull(page) || page < 0)
            page = 0;
        if (Objects.isNull(size) || size < 0)
            size = 10;
        var tags = tagRepository.findAll(PageRequest.of(page, size)).getContent().stream().map(TagDto::new).collect(Collectors.toList());
        return ResponseEntity.ok(tags);
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getTag(@PathVariable(name = "id") Long id) throws TagNotFoundException {
        Tag tag = tagRepository.findById(id).orElseThrow(TagNotFoundException::new);
        return ResponseEntity.ok(new TagDto(tag));
    }

    @PostMapping
    public ResponseEntity<?> postTag(@RequestBody TagForm form){
        Tag tag = form.toTag();
        tag.setName(new Slugify().slugify(tag.getName()));
        Tag tryFindTag = tagRepository.findByName(tag.getName()).orElse(null);
        if (tryFindTag != null)
            return new ResponseEntity<>(HttpStatus.CONFLICT);

        tagRepository.save(tag);
        return new ResponseEntity<>(new TagDto(tag), HttpStatus.CREATED);
    }
}
