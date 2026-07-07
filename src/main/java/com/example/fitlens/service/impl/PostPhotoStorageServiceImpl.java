package com.example.fitlens.service.impl;

import com.example.fitlens.config.UploadProperties;
import com.example.fitlens.service.PostPhotoStorageService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostPhotoStorageServiceImpl implements PostPhotoStorageService {

    private final UploadProperties uploadProperties;
    private Path postsDir;

    @PostConstruct
    void init() throws IOException {
        postsDir = Path.of(uploadProperties.dir(), "posts").toAbsolutePath().normalize();
        Files.createDirectories(postsDir);
    }

    @Override
    public String savePhoto(Long postId, String photoBase64) {
        if (photoBase64 == null || photoBase64.isBlank()) {
            return null;
        }
        try {
            String payload = photoBase64.trim();
            if (payload.contains(",")) {
                payload = payload.substring(payload.indexOf(',') + 1);
            }
            byte[] bytes = Base64.getDecoder().decode(payload);
            String relative = "posts/" + postId + ".jpg";
            Path target = postsDir.resolve(postId + ".jpg");
            Files.write(target, bytes);
            return relative;
        } catch (IllegalArgumentException | IOException ex) {
            throw new IllegalArgumentException("Invalid photo data.");
        }
    }

    @Override
    public Optional<Path> resolvePhotoPath(String photoPath) {
        if (photoPath == null || photoPath.isBlank()) {
            return Optional.empty();
        }
        Path resolved = Path.of(uploadProperties.dir(), photoPath).toAbsolutePath().normalize();
        if (!resolved.startsWith(Path.of(uploadProperties.dir()).toAbsolutePath().normalize())) {
            return Optional.empty();
        }
        return Files.exists(resolved) ? Optional.of(resolved) : Optional.empty();
    }

    @Override
    public byte[] readPhoto(String photoPath) {
        Path path = resolvePhotoPath(photoPath)
                .orElseThrow(() -> new IllegalArgumentException("Photo not found"));
        try {
            return Files.readAllBytes(path);
        } catch (IOException ex) {
            throw new IllegalArgumentException("Photo not found");
        }
    }
}
