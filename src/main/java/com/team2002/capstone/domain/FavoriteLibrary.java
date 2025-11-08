package com.team2002.capstone.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.team2002.capstone.dto.FavoriteLibrarySaveRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(uniqueConstraints = { // 중복 저장 기준
        @UniqueConstraint(columnNames = {"profile_id", "libName"})
})
public class FavoriteLibrary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long favoriteId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    @JsonBackReference("profile-favorites")
    private Profile profile;

    @Column(nullable = false)
    private String libName;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    public FavoriteLibrary(Profile profile, FavoriteLibrarySaveRequestDto dto) {
        this.profile = profile;
        this.libName = dto.getLibName();
        this.createdAt = LocalDateTime.now();
    }
}
