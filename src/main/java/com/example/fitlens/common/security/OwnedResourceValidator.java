package com.example.fitlens.common.security;

import com.example.fitlens.exception.ResourceNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class OwnedResourceValidator {

    public void assertSameOwner(Long ownerId, Long currentUserId, String resourceType, Long resourceId) {
        if (!ownerId.equals(currentUserId)) {
            throw new ResourceNotFoundException(resourceType + " not found: " + resourceId);
        }
    }
}
