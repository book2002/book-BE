package com.team2002.capstone.controller;


import com.team2002.capstone.dto.FavoriteLibraryResponseDto;
import com.team2002.capstone.dto.FavoriteLibrarySaveRequestDto;
import com.team2002.capstone.service.FavoriteLibraryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/favorites/libraries")
public class FavoriteLibraryController {

    private final FavoriteLibraryService favoriteLibraryService;

    @PostMapping
    public ResponseEntity<?> addFavorite(@Valid @RequestBody FavoriteLibrarySaveRequestDto requestDto) {
        try {
            FavoriteLibraryResponseDto responseDto = favoriteLibraryService.addFavorite(requestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        } catch (IllegalStateException e) {
            // 중복 예외 처리
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<FavoriteLibraryResponseDto>> getMyFavorites() {
        List<FavoriteLibraryResponseDto> myFavorites = favoriteLibraryService.getMyFavorites();
        return ResponseEntity.ok(myFavorites);
    }

    @DeleteMapping("/{favoriteId}")
    public ResponseEntity<Void> deleteFavorite(@PathVariable Long favoriteId) {
        favoriteLibraryService.deleteFavorite(favoriteId);
        return ResponseEntity.noContent().build();
    }


}
