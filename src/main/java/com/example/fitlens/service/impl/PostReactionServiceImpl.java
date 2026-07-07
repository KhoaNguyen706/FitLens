package com.example.fitlens.service.impl;

import com.example.fitlens.common.security.PostAccessValidator;
import com.example.fitlens.domain.entity.FitnessPost;
import com.example.fitlens.domain.entity.PostReaction;
import com.example.fitlens.domain.entity.User;
import com.example.fitlens.dto.request.AddPostReactionRequest;
import com.example.fitlens.exception.ResourceNotFoundException;
import com.example.fitlens.repository.FitnessPostRepository;
import com.example.fitlens.repository.PostReactionRepository;
import com.example.fitlens.service.PostReactionService;
import com.example.fitlens.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostReactionServiceImpl implements PostReactionService {

    private final UserService userService;
    private final FitnessPostRepository fitnessPostRepository;
    private final PostReactionRepository postReactionRepository;
    private final PostAccessValidator postAccessValidator;

    @Override
    @Transactional
    public PostReaction addReaction(Long userId, Long postId, AddPostReactionRequest request) {
        User user = userService.getById(userId);
        FitnessPost post = fitnessPostRepository.findByIdWithDetails(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found: " + postId));

        postAccessValidator.assertCanView(userId, post);

        PostReaction reaction = postReactionRepository.findByPostIdAndUserId(postId, userId)
                .orElseGet(() -> {
                    PostReaction created = new PostReaction();
                    created.setPost(post);
                    created.setUser(user);
                    return created;
                });

        reaction.setEmoji(request.emoji().trim());
        return postReactionRepository.save(reaction);
    }

    @Override
    @Transactional
    public void removeReaction(Long userId, Long postId) {
        userService.getById(userId);
        FitnessPost post = fitnessPostRepository.findByIdWithDetails(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found: " + postId));

        postAccessValidator.assertCanView(userId, post);

        postReactionRepository.deleteByPostIdAndUserId(postId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostReaction> getReactionsForPost(Long postId) {
        return postReactionRepository.findByPostIdWithUsers(postId);
    }
}
