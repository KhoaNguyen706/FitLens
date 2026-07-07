package com.example.fitlens.service.impl;

import com.example.fitlens.common.security.OwnedResourceValidator;
import com.example.fitlens.domain.entity.MealEntry;
import com.example.fitlens.domain.entity.MealPhoto;
import com.example.fitlens.domain.entity.User;
import com.example.fitlens.domain.enums.PhotoSource;
import com.example.fitlens.dto.request.CreateCloudMealPhotoRequest;
import com.example.fitlens.dto.request.CreateLocalMealPhotoRequest;
import com.example.fitlens.exception.ResourceNotFoundException;
import com.example.fitlens.repository.MealPhotoRepository;
import com.example.fitlens.service.MealEntryService;
import com.example.fitlens.service.MealPhotoService;
import com.example.fitlens.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MealPhotoServiceImpl implements MealPhotoService {

    private final UserService userService;
    private final MealEntryService mealEntryService;
    private final MealPhotoRepository mealPhotoRepository;
    private final OwnedResourceValidator ownedResourceValidator;

    @Override
    @Transactional(readOnly = true)
    public MealPhoto getById(Long userId, Long mealPhotoId) {
        return getOwnedMealPhoto(userId, mealPhotoId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MealPhoto> getPhotosForUser(Long userId) {
        userService.getById(userId);
        return mealPhotoRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public MealPhoto getPhotoForMeal(Long userId, Long mealEntryId) {
        mealEntryService.getById(userId, mealEntryId);

        return mealPhotoRepository.findByMealEntryId(mealEntryId)
                .orElseThrow(() -> new ResourceNotFoundException("Meal photo not found for meal: " + mealEntryId));
    }

    @Override
    @Transactional
    public MealPhoto createLocalPhoto(Long userId, CreateLocalMealPhotoRequest request) {
        MealPhoto mealPhoto = buildBasePhoto(userId, request.mealEntryId());
        mealPhoto.setLocalUri(request.localUri());
        mealPhoto.setLocalAssetId(request.localAssetId());
        mealPhoto.setPhotoSource(PhotoSource.LOCAL);
        return mealPhotoRepository.save(mealPhoto);
    }

    @Override
    @Transactional
    public MealPhoto createCloudPhoto(Long userId, CreateCloudMealPhotoRequest request) {
        MealPhoto mealPhoto = buildBasePhoto(userId, request.mealEntryId());
        mealPhoto.setStorageProvider(request.storageProvider());
        mealPhoto.setStoragePath(request.storagePath());
        mealPhoto.setPhotoSource(PhotoSource.CLOUD);
        return mealPhotoRepository.save(mealPhoto);
    }

    @Override
    @Transactional
    public void deletePhoto(Long userId, Long mealPhotoId) {
        MealPhoto mealPhoto = getOwnedMealPhoto(userId, mealPhotoId);
        mealPhotoRepository.delete(mealPhoto);
    }

    private MealPhoto buildBasePhoto(Long userId, Long mealEntryId) {
        User user = userService.getById(userId);
        MealEntry mealEntry = mealEntryId != null ? mealEntryService.getById(userId, mealEntryId) : null;

        MealPhoto mealPhoto = new MealPhoto();
        mealPhoto.setUser(user);
        mealPhoto.setMealEntry(mealEntry);
        return mealPhoto;
    }

    private MealPhoto getOwnedMealPhoto(Long userId, Long mealPhotoId) {
        MealPhoto mealPhoto = mealPhotoRepository.findFetchedById(mealPhotoId)
                .orElseThrow(() -> new ResourceNotFoundException("Meal photo not found: " + mealPhotoId));

        ownedResourceValidator.assertSameOwner(mealPhoto.getUser().getId(), userId, "Meal photo", mealPhotoId);
        return mealPhoto;
    }
}
