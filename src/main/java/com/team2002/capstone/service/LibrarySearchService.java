package com.team2002.capstone.service;

import com.team2002.capstone.dto.LibraryDto;
import com.team2002.capstone.dto.LibraryResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LibrarySearchService {

    private final WebClient webClient;
    private final String NARU_API_KEY = "30c1aec048afddf4aa40d1785eaf9e9af71d0a20ebc7352cbfcaee08dfed0e66"; // 본인 키
    private final String NARU_API_BASE_URL = "http://data4library.kr";

    public LibrarySearchService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(NARU_API_BASE_URL).build();
    }

    // ▼▼▼ 메소드 시그니처 수정 ▼▼▼
    public List<LibraryDto> searchLibraries(String region, String keyword) {

        // 지역 코드가 없으면 API를 호출하지 않음 (Controller에서 이미 막았지만, 2차 방어)
        if (region == null || region.trim().isEmpty()) {
            throw new IllegalArgumentException("지역 코드는 필수입니다.");
        }

        try {
            LibraryResponseDto response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/libSrch")
                            .queryParam("authKey", NARU_API_KEY)
                            // ▼▼▼ 'region' 파라미터를 항상 API에 전달 ▼▼▼
                            .queryParam("region", region)
                            .queryParam("pageSize", 100) // 해당 지역 도서관을 100개까지 받음
                            .queryParam("format", "json")
                            .build())
                    .retrieve()
                    .bodyToMono(LibraryResponseDto.class)
                    .block();

            if (response != null && response.getResponse() != null &&
                    response.getResponse().getLibs() != null) {

                // 1. API가 "해당 지역"으로 1차 필터링한 목록을 추출
                List<LibraryDto> regionList = response.getResponse().getLibs().stream()
                        .map(LibraryResponseDto.LibWrapper::getLib)
                        .collect(Collectors.toList());

                // 2. ▼▼▼ Java 코드로 2차 이름(keyword) 필터링 (선택적) ▼▼▼
                // keyword가 (null이나 빈 값이 아닌) 유효한 값이면,
                if (keyword != null && !keyword.trim().isEmpty()) {
                    return regionList.stream()
                            .filter(lib -> lib.getLibName() != null && lib.getLibName().contains(keyword))
                            .collect(Collectors.toList());
                }

                // keyword가 없으면, 1차 필터링된 지역 목록 전체를 반환
                return regionList;
            }

            return Collections.emptyList(); // 결과가 없으면 빈 리스트 반환

        } catch (Exception e) {
            System.err.println("도서관 정보나루 API 호출 중 에러 발생: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}