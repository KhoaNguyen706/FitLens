package com.example.fitlens.repository;

import com.example.fitlens.domain.entity.Friendship;
import com.example.fitlens.domain.enums.FriendshipStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    @Query("""
            SELECT f FROM Friendship f
            JOIN FETCH f.requester
            JOIN FETCH f.receiver
            WHERE f.receiver.id = :receiverId AND f.status = :status
            ORDER BY f.createdAt DESC
            """)
    List<Friendship> findPendingRequestsForReceiver(
            @Param("receiverId") Long receiverId,
            @Param("status") FriendshipStatus status
    );

    @Query("""
            SELECT f FROM Friendship f
            JOIN FETCH f.requester
            JOIN FETCH f.receiver
            WHERE f.status = :status
              AND (f.requester.id = :userId OR f.receiver.id = :userId)
            ORDER BY f.updatedAt DESC
            """)
    List<Friendship> findAcceptedFriendships(@Param("userId") Long userId, @Param("status") FriendshipStatus status);

    @Query("""
            SELECT f FROM Friendship f
            JOIN FETCH f.requester
            JOIN FETCH f.receiver
            WHERE (f.requester.id = :userId1 AND f.receiver.id = :userId2)
               OR (f.requester.id = :userId2 AND f.receiver.id = :userId1)
            """)
    Optional<Friendship> findBetweenUsers(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    @Query("""
            SELECT f FROM Friendship f
            JOIN FETCH f.requester
            JOIN FETCH f.receiver
            WHERE f.id = :id
            """)
    Optional<Friendship> findByIdWithUsers(@Param("id") Long id);

    @Query("""
            SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END
            FROM Friendship f
            WHERE f.status = 'ACCEPTED'
              AND ((f.requester.id = :userId1 AND f.receiver.id = :userId2)
                OR (f.requester.id = :userId2 AND f.receiver.id = :userId1))
            """)
    boolean areAcceptedFriends(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
}
