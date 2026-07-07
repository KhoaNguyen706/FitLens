package com.example.fitlens.service;

import com.example.fitlens.domain.entity.FitnessPost;
import com.example.fitlens.dto.request.CreateFitnessPostRequest;

import java.util.List;

public interface FitnessPostService {

    FitnessPost createPost(Long userId, CreateFitnessPostRequest request);

    List<FitnessPost> getCloseFriendsFeed(Long userId);

    FitnessPost getPost(Long userId, Long postId);
}
