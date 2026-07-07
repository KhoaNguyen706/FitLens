package com.example.fitlens.service;

import java.nio.file.Path;
import java.util.Optional;

public interface PostPhotoStorageService {

    String savePhoto(Long postId, String photoBase64);

    Optional<Path> resolvePhotoPath(String photoPath);

    byte[] readPhoto(String photoPath);
}
