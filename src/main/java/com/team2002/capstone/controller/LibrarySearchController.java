package com.team2002.capstone.controller;

import com.team2002.capstone.dto.LibraryDto;

import com.team2002.capstone.service.LibrarySearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/libraries")
public class LibrarySearchController {

    private final LibrarySearchService librarySearchService;

    /**
     * '도서관 정보나루' API를 이용한 도서관 목록 검색
     * @param region (필수) 검색할 지역 코드 (예: 11=서울, 26=부산)
     * @param keyword (선택) 지역 내에서 추가로 검색할 도서관 이름
     * @return 도서관 목록 (JSON)
     */
    @GetMapping("/search")
    public ResponseEntity<List<LibraryDto>> searchLibraries(
            // ▼▼▼ 'region'을 필수 파라미터로 변경 (required=true가 기본값) ▼▼▼
            @RequestParam("region") String region,
            // ▼▼▼ 'keyword'는 선택 파라미터로 유지 ▼▼▼
            @RequestParam(value = "keyword", required = false) String keyword) {

        List<LibraryDto> libraries = librarySearchService.searchLibraries(region, keyword);
        return ResponseEntity.ok(libraries);
    }
}