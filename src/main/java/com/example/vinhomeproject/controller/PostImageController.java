package com.example.vinhomeproject.controller;

import com.example.vinhomeproject.models.PostImage;
import com.example.vinhomeproject.response.ResponseObject;
import com.example.vinhomeproject.service.PostImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/postimage")
public class PostImageController {
    private PostImageService sv;
    @Autowired
    public void PostImageSerivce(PostImageService sv){this.sv=sv;}
    @GetMapping
    public ResponseEntity<ResponseObject> getPostImage(){return sv.getAllPostImage();}
    @PutMapping("/delete")
    public ResponseEntity<String> deletePostImage(Long id){return sv.deletePostImage(id);}
    @PutMapping("/update")
    public ResponseEntity<String> updatePostImage(PostImage ps){return sv.updatePostImage(ps);}
    @PostMapping("/create")
    public ResponseEntity<String> createPostImage(@RequestParam("file") MultipartFile multipartFile){return sv.createPostImage(multipartFile);}
}
