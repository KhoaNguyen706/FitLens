package com.example.fitlens.service;

import com.example.fitlens.domain.entity.PostReaction;
import com.example.fitlens.dto.request.AddPostReactionRequest;

import java.util.List;

public interface PostReactionService {

    PostReaction addReaction(Long userId, Long postId, AddPostReactionRequest request);

    void removeReaction(Long userId, Long postId);

    List<PostReaction> getReactionsForPost(Long postId);
}
