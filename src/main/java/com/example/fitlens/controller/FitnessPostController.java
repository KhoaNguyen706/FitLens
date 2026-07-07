package com.example.fitlens.controller;

import com.example.fitlens.common.web.ApiResponseFactory;
import com.example.fitlens.domain.entity.FitnessPost;
import com.example.fitlens.domain.entity.PostReaction;
import com.example.fitlens.dto.request.AddPostReactionRequest;
import com.example.fitlens.dto.request.CreateFitnessPostRequest;
import com.example.fitlens.dto.response.ApiResponse;
import com.example.fitlens.dto.response.FitnessPostResponse;
import com.example.fitlens.mapper.FitnessPostMapper;
import com.example.fitlens.security.AuthUser;
import com.example.fitlens.service.FitnessPostService;
import com.example.fitlens.service.PostPhotoStorageService;
import com.example.fitlens.service.PostReactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FitnessPostController {

    private final FitnessPostService fitnessPostService;
    private final PostReactionService postReactionService;
    private final PostPhotoStorageService postPhotoStorageService;
    private final FitnessPostMapper fitnessPostMapper;
    private final ApiResponseFactory responses;

    @PostMapping("/posts")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<FitnessPostResponse> createPost(
            @AuthenticationPrincipal AuthUser authUser,
            @Valid @RequestBody CreateFitnessPostRequest request
    ) {
        FitnessPost post = fitnessPostService.createPost(authUser.id(), request);
        List<PostReaction> reactions = postReactionService.getReactionsForPost(post.getId());
        return responses.ok(
                "Post created",
                fitnessPostMapper.toDetailResponse(post, reactions, authUser.id())
        );
    }

    @GetMapping("/feed")
    public ApiResponse<List<FitnessPostResponse>> getFeed(@AuthenticationPrincipal AuthUser authUser) {
        List<FitnessPost> posts = fitnessPostService.getCloseFriendsFeed(authUser.id());
        List<FitnessPostResponse> feed = posts.stream()
                .map(post -> {
                    List<PostReaction> reactions = postReactionService.getReactionsForPost(post.getId());
                    return fitnessPostMapper.toFeedResponse(post, reactions, authUser.id());
                })
                .toList();
        return responses.ok(feed);
    }

    @GetMapping("/posts/{id}")
    public ApiResponse<FitnessPostResponse> getPost(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long id
    ) {
        FitnessPost post = fitnessPostService.getPost(authUser.id(), id);
        List<PostReaction> reactions = postReactionService.getReactionsForPost(id);
        return responses.ok(fitnessPostMapper.toDetailResponse(post, reactions, authUser.id()));
    }

    @GetMapping("/posts/{id}/photo")
    public ResponseEntity<byte[]> getPostPhoto(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long id
    ) {
        FitnessPost post = fitnessPostService.getPost(authUser.id(), id);
        if (post.getPhotoPath() == null || post.getPhotoPath().isBlank()) {
            return ResponseEntity.notFound().build();
        }
        byte[] bytes = postPhotoStorageService.readPhoto(post.getPhotoPath());
        return ResponseEntity.ok()
                .header(HttpHeaders.CACHE_CONTROL, "private, max-age=3600")
                .contentType(MediaType.IMAGE_JPEG)
                .body(bytes);
    }

    @PostMapping("/posts/{id}/reactions")
    public ApiResponse<FitnessPostResponse> addReaction(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long id,
            @Valid @RequestBody AddPostReactionRequest request
    ) {
        postReactionService.addReaction(authUser.id(), id, request);
        FitnessPost post = fitnessPostService.getPost(authUser.id(), id);
        List<PostReaction> reactions = postReactionService.getReactionsForPost(id);
        return responses.ok(
                "Reaction added",
                fitnessPostMapper.toDetailResponse(post, reactions, authUser.id())
        );
    }

    @DeleteMapping("/posts/{id}/reactions")
    public ApiResponse<FitnessPostResponse> removeReaction(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long id
    ) {
        postReactionService.removeReaction(authUser.id(), id);
        FitnessPost post = fitnessPostService.getPost(authUser.id(), id);
        List<PostReaction> reactions = postReactionService.getReactionsForPost(id);
        return responses.ok(
                "Reaction removed",
                fitnessPostMapper.toDetailResponse(post, reactions, authUser.id())
        );
    }
}
