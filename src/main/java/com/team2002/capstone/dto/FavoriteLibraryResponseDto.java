package com.team2002.capstone.dto;

import com.team2002.capstone.domain.FavoriteLibrary;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class FavoriteLibraryResponseDto {
    private Long favoriteId;
    private String libName;
    private LocalDateTime createdAt;


    public FavoriteLibraryResponseDto(FavoriteLibrary favorite) {
        this.favoriteId = favorite.getFavoriteId();
        this.libName = favorite.getLibName();
        this.createdAt = favorite.getCreatedAt();
    }
}
