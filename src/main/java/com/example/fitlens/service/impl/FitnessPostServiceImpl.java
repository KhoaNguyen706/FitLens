package com.example.fitlens.service.impl;

import com.example.fitlens.common.security.PostAccessValidator;
import com.example.fitlens.domain.entity.FitnessPost;
import com.example.fitlens.domain.entity.MealEntry;
import com.example.fitlens.domain.entity.User;
import com.example.fitlens.domain.enums.PostVisibility;
import com.example.fitlens.dto.request.CreateFitnessPostRequest;
import com.example.fitlens.exception.ResourceNotFoundException;
import com.example.fitlens.repository.FitnessPostRepository;
import com.example.fitlens.service.FitnessPostService;
import com.example.fitlens.service.FriendshipService;
import com.example.fitlens.service.MealEntryService;
import com.example.fitlens.service.PostPhotoStorageService;
import com.example.fitlens.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FitnessPostServiceImpl implements FitnessPostService {

    private final UserService userService;
    private final MealEntryService mealEntryService;
    private final FriendshipService friendshipService;
    private final FitnessPostRepository fitnessPostRepository;
    private final PostAccessValidator postAccessValidator;
    private final PostPhotoStorageService postPhotoStorageService;

    @Override
    @Transactional
    public FitnessPost createPost(Long userId, CreateFitnessPostRequest request) {
        User user = userService.getById(userId);

        FitnessPost post = new FitnessPost();
        post.setUser(user);
        post.setCaption(request.caption());
        post.setVisibility(request.visibility());

        if (request.mealEntryId() != null) {
            MealEntry mealEntry = mealEntryService.getById(userId, request.mealEntryId());
            post.setMealEntry(mealEntry);
        }

        FitnessPost saved = fitnessPostRepository.save(post);

        if (request.photoBase64() != null && !request.photoBase64().isBlank()) {
            String photoPath = postPhotoStorageService.savePhoto(saved.getId(), request.photoBase64());
            saved.setPhotoPath(photoPath);
            saved = fitnessPostRepository.save(saved);
        }

        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public List<FitnessPost> getCloseFriendsFeed(Long userId) {
        userService.getById(userId);
        List<Long> authorIds = new ArrayList<>(collectFriendUserIds(userId));
        if (!authorIds.contains(userId)) {
            authorIds.add(userId);
        }
        return fitnessPostRepository.findFeedPosts(
                authorIds,
                List.of(PostVisibility.FRIENDS, PostVisibility.CLOSE_FRIENDS)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public FitnessPost getPost(Long userId, Long postId) {
        FitnessPost post = fitnessPostRepository.findByIdWithDetails(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found: " + postId));

        assertCanView(userId, post);
        return post;
    }

    void assertCanView(Long viewerUserId, FitnessPost post) {
        postAccessValidator.assertCanView(viewerUserId, post);
    }

    private List<Long> collectFriendUserIds(Long userId) {
        return friendshipService.getFriends(userId).stream()
                .map(f -> f.getRequester().getId().equals(userId)
                        ? f.getReceiver().getId()
                        : f.getRequester().getId())
                .toList();
    }
}
