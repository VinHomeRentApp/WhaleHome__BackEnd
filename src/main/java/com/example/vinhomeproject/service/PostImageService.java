package com.example.vinhomeproject.service;

import com.example.vinhomeproject.models.Post;
import com.example.vinhomeproject.models.PostImage;
import com.example.vinhomeproject.repositories.PostImageRepository;
import com.example.vinhomeproject.repositories.PostRepository;
import com.example.vinhomeproject.response.ResponseObject;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Optional;
import java.util.UUID;

@Service
public class PostImageService {
    @Autowired
    private  PostImageRepository rs;
    @Autowired
    private PostRepository postRepository;


    public ResponseEntity<ResponseObject> getAllPostImage() {
        return ResponseEntity.ok(new ResponseObject(
                "successfully",
                rs.findAll()
        ));
    }

    public ResponseEntity<ResponseObject> getPostImageById(Long id) {

        return ResponseEntity.ok(new ResponseObject(
                "successfully",
                rs.findPostImageById(id)
        ));
    }

    public ResponseEntity<String> deletePostImage(Long id) {
        PostImage ex = rs.findPostImageById(id);
        if (ex != null) {
            ex.setStatus(!ex.isStatus());
            return ResponseEntity.ok("Delete successfully");
        }else {
            return ResponseEntity.ok("id not exist");
        }
    }

    public ResponseEntity<String> updatePostImage(PostImage postImage) {
        PostImage ps = rs.findPostImageById(postImage.getId());
        if (ps != null) {
            if(postImage.getModifiedBy()!=null){ps.setModifiedBy(postImage.getModifiedBy());}
            if(postImage.getCreateBy()!=null){ps.setCreateBy(postImage.getCreateBy());}
            if(postImage.getCreateDate()!=null){ps.setCreateDate(postImage.getCreateDate());}

            if(postImage.getImage_alt()!=null){ps.setImage_alt(postImage.getImage_alt());}
            if(postImage.getImage_url()!=null){ps.setImage_url(postImage.getImage_url());}
            rs.save(ps);
            return ResponseEntity.ok("update successfully");
        }else {
            return ResponseEntity.ok("id not exist");
        }

    }

    public ResponseEntity<String> createPostImage(MultipartFile multipartFile, Long id) {
        Optional<Post> post = postRepository.findById(id);
        if(post.isPresent()){
            PostImage ps = new PostImage();
            if (multipartFile != null) {
                String imageUrl = this.upload(multipartFile);
                ps.setImage_url(imageUrl);
                ps.setPost(post.get());
                ps.setImage_alt("image");
                ps.setStatus(true);
                rs.save(ps);
                return ResponseEntity.ok("Image uploaded successfully. Image URL: " + imageUrl);
            } else {
                return ResponseEntity.badRequest().body("PostImage object is null");
            }
        }
        return ResponseEntity.badRequest().body("Post is not exist");
    }

    private File convertToFile(MultipartFile multipartFile, String fileName) throws IOException {
        File tempFile = new File(fileName);
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(multipartFile.getBytes());
        }
        return tempFile;
    }

    private String uploadFile(File file, String fileName) throws IOException {
        BlobId blobId = BlobId.of("whalehome-project.appspot.com", fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("media").build();
        InputStream inputStream = PostImageService.class.getClassLoader().getResourceAsStream("firebase.json");
        Credentials credentials = GoogleCredentials.fromStream(inputStream);
        Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
        storage.create(blobInfo, Files.readAllBytes(file.toPath()));
        String DOWNLOAD_URL = "https://firebasestorage.googleapis.com/v0/b/whalehome-project.appspot.com/o/%s?alt=media";
        return String.format(DOWNLOAD_URL, URLEncoder.encode(fileName, StandardCharsets.UTF_8));
    }
    private String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }


    private String upload(MultipartFile multipartFile) {
        try {
            String fileName = multipartFile.getOriginalFilename();
            fileName = UUID.randomUUID().toString().concat(this.getExtension(fileName));

            File file = this.convertToFile(multipartFile, fileName);
            String URL = this.uploadFile(file, fileName);
            file.delete();
            return URL;
        } catch (Exception e) {
            e.printStackTrace();
            return "Image couldn't upload, Something went wrong";
        }
    }

}
