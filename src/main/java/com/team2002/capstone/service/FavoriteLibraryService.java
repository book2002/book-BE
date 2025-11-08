package com.team2002.capstone.service;

import com.team2002.capstone.domain.FavoriteLibrary;
import com.team2002.capstone.domain.Member;
import com.team2002.capstone.domain.Profile;
import com.team2002.capstone.dto.FavoriteLibraryResponseDto;
import com.team2002.capstone.dto.FavoriteLibrarySaveRequestDto;
import com.team2002.capstone.exception.ResourceNotFoundException;
import com.team2002.capstone.repository.FavoriteLibraryRepository;

import com.team2002.capstone.repository.MemberRepository;
import com.team2002.capstone.repository.ProfileRepository;
import com.team2002.capstone.util.SecurityUtil;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FavoriteLibraryService {
    private final FavoriteLibraryRepository favoriteLibraryRepository;
    private final ProfileRepository profileRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public FavoriteLibraryResponseDto addFavorite(FavoriteLibrarySaveRequestDto requestDto) {
        Profile profile = getCurrentProfile();
        favoriteLibraryRepository.findByProfileAndLibName(profile, requestDto.getLibName())
                .ifPresent(fav -> {
                    throw new IllegalStateException("이미 즐겨찾기에 추가된 도서관입니다.");
                });

        FavoriteLibrary newFavorite = new FavoriteLibrary(profile, requestDto);

        FavoriteLibrary savedFavorite = favoriteLibraryRepository.save(newFavorite);
        return new FavoriteLibraryResponseDto(savedFavorite);
    }


    public List<FavoriteLibraryResponseDto> getMyFavorites() {
        Profile profile = getCurrentProfile();
        List<FavoriteLibrary> favorites = favoriteLibraryRepository.findByProfile(profile);
        return favorites.stream()
                .map(FavoriteLibraryResponseDto::new)
                .collect(Collectors.toList());

    }

    @Transactional
    public void deleteFavorite(Long favoriteId) {
        Profile profile = getCurrentProfile();
        FavoriteLibrary favorite = favoriteLibraryRepository.findById(favoriteId)
                .orElseThrow(() -> new ResourceNotFoundException("즐겨찾기 항목을 찾을 수 없습니다."));

        // 소유권 검사
        if (!Objects.equals(favorite.getProfile().getId(), profile.getId())) {
            throw new IllegalStateException("이 즐겨찾기를 삭제할 권한이 없습니다.");
        }

        favoriteLibraryRepository.delete(favorite);
    }

    private Profile getCurrentProfile() {
        String userEmail = SecurityUtil.getCurrentUsername();

        Member member = memberRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("회원을 찾을 수 없습니다."));

        // Member 엔티티에서 Profile을 찾아 반환
        return profileRepository.findByMember(member)
                .orElseThrow(() -> new ResourceNotFoundException("현재 로그인한 프로필을 찾을수 없습니다."));
    }

}
