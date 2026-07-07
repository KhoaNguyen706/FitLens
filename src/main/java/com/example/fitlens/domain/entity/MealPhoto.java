package com.example.fitlens.domain.entity;

import com.example.fitlens.domain.enums.PhotoSource;
import com.example.fitlens.domain.enums.StorageProvider;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Table(name = "meal_photos")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
public class MealPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meal_entry_id")
    private MealEntry mealEntry;

    @Column(name = "local_uri", columnDefinition = "text")
    private String localUri;

    @Column(name = "local_asset_id")
    private String localAssetId;

    @Enumerated(EnumType.STRING)
    @Column(name = "storage_provider", length = 20)
    private StorageProvider storageProvider;

    @Column(name = "storage_path", columnDefinition = "text")
    private String storagePath;

    @Enumerated(EnumType.STRING)
    @Column(name = "photo_source", nullable = false, length = 10)
    private PhotoSource photoSource;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
